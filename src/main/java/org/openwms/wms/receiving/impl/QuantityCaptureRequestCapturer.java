/*
 * Copyright 2005-2022 the original author or authors.
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
import org.openwms.wms.receiving.ProcessingException;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.QuantityCaptureRequestVO;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.inventory.AsyncPackagingUnitApi;
import org.openwms.wms.receiving.spi.wms.inventory.CreatePackagingUnitCommand;
import org.openwms.wms.receiving.spi.wms.inventory.PackagingUnitVO;
import org.openwms.wms.receiving.spi.wms.inventory.ProductVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static org.openwms.wms.order.OrderState.CREATED;
import static org.openwms.wms.order.OrderState.PROCESSING;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_UNEXPECTED_ALLOWED;

/**
 * A QuantityCaptureRequestCapturer.
 *
 * @author Heiko Scherrer
 */
@TxService
class QuantityCaptureRequestCapturer extends AbstractCapturer implements ReceivingOrderCapturer<QuantityCaptureRequestVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityCaptureRequestCapturer.class);
    private final boolean overbookingAllowed;
    private final AsyncPackagingUnitApi asyncPackagingUnitApi;

    QuantityCaptureRequestCapturer(@Value("${owms.receiving.unexpected-receipts-allowed:true}") boolean overbookingAllowed,
            Translator translator, ReceivingOrderRepository repository, ProductService productService,
            AsyncPackagingUnitApi asyncPackagingUnitApi) {
        super(translator, repository, productService);
        this.overbookingAllowed = overbookingAllowed;
        this.asyncPackagingUnitApi = asyncPackagingUnitApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull ReceivingOrder capture(@NotBlank String pKey, @Valid @NotNull QuantityCaptureRequestVO request) {
        final var sku = request.getProduct().getSku();
        final var quantityReceived = request.getQuantityReceived();
        final var transportUnitId = request.getTransportUnitId();
        final var loadUnitPosition = request.getLoadUnitLabel();
        final var existingProduct = getProduct(sku);
        final var details = request.getDetails();
        var receivingOrder = getOrder(pKey);
        var openPositions = receivingOrder.getPositions().stream()
                .filter(p -> p.getState() == CREATED || p.getState() == PROCESSING)
                .filter(ReceivingOrderPosition.class::isInstance)
                .map(ReceivingOrderPosition.class::cast)
                .filter(p -> p.getProduct().equals(existingProduct))
                .toList();

        if (openPositions.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded Product exist");
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }
        var openPosition = openPositions.stream()
                .filter(p -> p.getQuantityExpected().getUnit().equals(quantityReceived.getUnit()))
                .filter(p -> p.getQuantityExpected().getValue().doubleValue() >= quantityReceived.getValue().doubleValue())
                .findFirst();
        ReceivingOrderPosition position;
        // Got an unexpected receipt. If this is configured to be okay we proceed otherwise throw
        if (openPosition.isEmpty()) {
            if (overbookingAllowed) {
                position = openPositions.get(0);
            } else {
                LOGGER.error("Received a goods receipt but all ReceivingOrderPositions are already satisfied and unexpected receipts are not allowed");
                throw new ProcessingException(translator, RO_NO_UNEXPECTED_ALLOWED, new String[0]);
            }
        } else {
            position = openPosition.get();
        }
        for (int i = 0; i < quantityReceived.getValue().intValue(); i++) {
            // single packs
            var pu = new PackagingUnitVO(
                    ProductVO.newBuilder().sku(sku).build(),
                    existingProduct.getBaseUnit()
            );
            pu.setDetails(details);
            pu.setSerialNumber(request.getSerialNumber());
            pu.setLotId(request.getLotId());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Create new PackagingUnit [{}] on TransportUnit [{}] and LoadUnit [{}]", pu, transportUnitId, loadUnitPosition);
            }
            asyncPackagingUnitApi.create(new CreatePackagingUnitCommand(transportUnitId, loadUnitPosition, request.getLoadUnitType(), pu));
        }
        position.addQuantityReceived(quantityReceived);
        receivingOrder = repository.save(receivingOrder);
        return receivingOrder;
    }

    @Override
    public boolean supports(CaptureRequestVO request) {
        return request instanceof QuantityCaptureRequestVO;
    }
}
