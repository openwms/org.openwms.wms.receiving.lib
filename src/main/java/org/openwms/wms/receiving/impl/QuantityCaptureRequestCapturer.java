/*
 * Copyright 2005-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.wms.receiving.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.i18n.Translator;
import org.ameba.system.ValidationUtil;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.PositionState;
import org.openwms.wms.receiving.api.QuantityCaptureRequestVO;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.inventory.AsyncPackagingUnitApi;
import org.openwms.wms.receiving.spi.wms.inventory.CreatePackagingUnitCommand;
import org.openwms.wms.receiving.spi.wms.inventory.PackagingUnitVO;
import org.openwms.wms.receiving.spi.wms.inventory.ProductVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Consumer;

import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_UNEXPECTED_ALLOWED;

/**
 * A QuantityCaptureRequestCapturer accepts capturing inbound goods on a TransportUnit only.
 *
 * @author Heiko Scherrer
 */
@TxService
class QuantityCaptureRequestCapturer extends AbstractCapturer implements ReceivingOrderCapturer<QuantityCaptureRequestVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityCaptureRequestCapturer.class);
    private final AsyncPackagingUnitApi asyncPackagingUnitApi;

    QuantityCaptureRequestCapturer(Translator translator, ReceivingOrderRepository repository, ProductService productService,
            ApplicationEventPublisher publisher, Validator validator, AsyncPackagingUnitApi asyncPackagingUnitApi) {
        super(publisher, translator, validator, repository, productService);
        this.asyncPackagingUnitApi = asyncPackagingUnitApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<ReceivingOrder> capture(String pKey, @NotNull QuantityCaptureRequestVO request) {
        if (pKey != null) {
            ValidationUtil.validate(validator, request, ValidationGroups.CreateQuantityReceipt.class);
            return handleExpectedReceipt(
                    pKey,
                    request.getQuantityReceived(),
                    getProduct(request.getProduct().getSku()),
                    v -> createPackagingUnitsForDemand(request));
        }
        ValidationUtil.validate(validator, request, ValidationGroups.CreateQuantityReceipt.class);
        createPackagingUnitsForDemand(request);
        return Optional.empty();
    }

    private Optional<ReceivingOrder> handleExpectedReceipt(String pKey, Measurable quantityReceived, Product existingProduct,
            Consumer<Void> func) {
        var receivingOrder = getOrder(pKey);
        var openPositions = receivingOrder.getPositions().stream()
                .filter(AbstractReceivingOrderPosition::doesStateAllowCapturing)
                .filter(ReceivingOrderPosition.class::isInstance)
                .map(ReceivingOrderPosition.class::cast)
                .filter(p -> p.getProduct().equals(existingProduct))
                .toList();

        if (openPositions.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded Product [{}] exist", existingProduct.shortId());
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }

        var openPosition = openPositions.stream()
                .filter(p -> p.getQuantityExpected().getUnitType().equals(quantityReceived.getUnitType()))
                .filter(p -> p.getQuantityExpected().compareTo(quantityReceived) >= 0)
                .findFirst();
        ReceivingOrderPosition position;
        if (openPosition.isEmpty()) {
            // Have an expected position but quantity is already exceeded
            if (Boolean.TRUE.equals(openPositions.get(0).getProduct().getOverbookingAllowed())) {
                position = openPositions.get(0);
                LOGGER.debug("Overbooking for product [{}] is allowed, so capture on position [{}]",
                        openPositions.get(0).getProduct().shortId(), position.getPosNo());
            } else {
                LOGGER.error("Received a goods receipt but all ReceivingOrderPositions are already satisfied and unexpected receipts are not allowed for product [{}]",
                        existingProduct.shortId());
                throw new CapturingException(translator, RO_NO_UNEXPECTED_ALLOWED, new String[0]);
            }
        } else {
            position = openPosition.get();
            LOGGER.info("Capture on the first open position [{}]", position.getPosNo());
        }

        func.accept(null);

        position.addQuantityReceived(quantityReceived);
        LOGGER.debug("New quantity of position [{}] is set to [{}]", position.getPosNo(), position.getQuantityReceived());
        if (position.getQuantityReceived().compareTo(position.getQuantityExpected()) >= 0) {
            position.changePositionState(publisher, PositionState.COMPLETED);
        } else {
            position.changePositionState(publisher, PositionState.PROCESSING);
        }
        receivingOrder = repository.save(receivingOrder);
        return Optional.of(receivingOrder);
    }

    private void createPackagingUnitsForDemand(QuantityCaptureRequestVO request) {
        final var sku = request.getProduct().getSku();
        for (var i = 0; i < request.getQuantityReceived().getMagnitude().intValue(); i++) {
            // single packs
            var pu = new PackagingUnitVO(
                    ProductVO.newBuilder().sku(sku).build(),
                    getProduct(sku).getBaseUnit()
            );
            pu.setDetails(request.getDetails());
            pu.setSerialNumber(request.getSerialNumber());
            pu.setLotId(request.getLotId());
            asyncPackagingUnitApi.create(new CreatePackagingUnitCommand(
                    request.getTransportUnit().getTransportUnitId(),
                    request.getLoadUnitLabel(),
                    request.getLoadUnitType(), pu)
            );
        }
    }

    @Override
    public boolean supports(CaptureRequestVO request) {
        return request instanceof QuantityCaptureRequestVO;
    }
}
