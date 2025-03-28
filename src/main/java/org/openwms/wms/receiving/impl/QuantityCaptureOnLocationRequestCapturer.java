/*
 * Copyright 2005-2025 the original author or authors.
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

import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.ameba.system.ValidationUtil;
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.LocationVO;
import org.openwms.wms.receiving.api.PositionState;
import org.openwms.wms.receiving.api.QuantityCaptureOnLocationRequestVO;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.inventory.PackagingUnitVO;
import org.openwms.wms.receiving.spi.wms.inventory.ProductVO;
import org.openwms.wms.receiving.spi.wms.inventory.SyncPackagingUnitApi;
import org.openwms.wms.receiving.spi.wms.inventory.SyncProductApi;
import org.openwms.wms.receiving.spi.wms.receiving.CapturingApproval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.openwms.wms.receiving.ReceivingMessages.PRODUCT_NOT_FOUND;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS;

/**
 * A QuantityCaptureOnLocationRequestCapturer accepts capturing inbound goods on a Location.
 *
 * @author Heiko Scherrer
 */
@TxService
class QuantityCaptureOnLocationRequestCapturer extends AbstractCapturer<QuantityCaptureOnLocationRequestVO> implements ReceivingOrderCapturer<QuantityCaptureOnLocationRequestVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityCaptureOnLocationRequestCapturer.class);
    private final SyncPackagingUnitApi packagingUnitApi;
    private final SyncProductApi productApi;

    QuantityCaptureOnLocationRequestCapturer(ApplicationEventPublisher publisher, Translator translator,
                                             ReceivingOrderRepository repository, ProductService productService,
                                             Validator validator,
                                             @Autowired(required = false) List<CapturingApproval<QuantityCaptureOnLocationRequestVO>> capturingApprovals,
                                             SyncPackagingUnitApi packagingUnitApi, SyncProductApi productApi) {
        super(publisher, translator, validator, repository, capturingApprovals, productService);
        this.packagingUnitApi = packagingUnitApi;
        this.productApi = productApi;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public Optional<ReceivingOrder> capture(String pKey, @NotNull QuantityCaptureOnLocationRequestVO request) {
        ValidationUtil.validate(validator, request, ValidationGroups.CreateQuantityReceipt.class);
        if (pKey != null) {
            return handleExpectedReceipt(
                    pKey,
                    request,
                    v -> createPackagingUnitsForDemand(request)
            );
        }
        createPackagingUnitsForDemand(request);
        return Optional.empty();
    }

    protected Product getProduct(QuantityCaptureOnLocationRequestVO request) {
        final var skuExistingProduct = request.hasUomRelation()
                ? Optional.ofNullable(productApi.findProductByProductUnitPkey(request.getUomRelation().pKey)).orElseThrow(ifNotFound(request)).getSku()
                : productService.findBySku(request.getProduct().getSku()).orElseThrow(ifNotFound(request)).getSku();
        return super.getProduct(skuExistingProduct);
    }

    private Supplier<NotFoundException> ifNotFound(QuantityCaptureOnLocationRequestVO request) {
        return () -> new NotFoundException(translator, PRODUCT_NOT_FOUND, new String[]{request.getProduct().getSku()},
                request.getProduct().getSku());
    }

    private void createPackagingUnitsForDemand(QuantityCaptureOnLocationRequestVO request) {
        final var erpCode = request.getActualLocation().getErpCode();
        final var quantityReceived = request.getQuantityReceived();
        // multi packs
        var pu = request.hasUomRelation()
                ? new PackagingUnitVO(request.getUomRelation(), quantityReceived)
                : new PackagingUnitVO(ProductVO.newBuilder().sku(request.getProduct().getSku()).build(), quantityReceived);
        pu.setActualLocation(new LocationVO(erpCode));
        pu.setDetails(request.getDetails());
        pu.setSerialNumber(request.getSerialNumber());
        pu.setLotId(request.getLotId());
        pu.setProduct(ProductVO.newBuilder().sku(request.getProduct().getSku()).build());
        pu.setExpiresAt(request.getExpiresAt());
        pu.setProductionDate(request.getProductionDate());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Create new PackagingUnit [{}] on Location [{}]", pu, erpCode);
        }
        packagingUnitApi.createOnLocation(asList(pu));
    }

    private Optional<ReceivingOrder> handleExpectedReceipt(String pKey, QuantityCaptureOnLocationRequestVO request, Consumer<Void> func) {
        var receivingOrder = getOrder(pKey);
        receivingOrder.getPositions().forEach(p -> capturingApprovals.forEach(ca -> ca.approve(p, request)));
        var existingProduct = getProduct(request);
        var openPositions = receivingOrder.getPositions().stream()
                .filter(AbstractReceivingOrderPosition::doesStateAllowCapturing)
                .filter(ReceivingOrderPosition.class::isInstance)
                .map(ReceivingOrderPosition.class::cast)
                .filter(p -> p.getProduct().getSku().equals(existingProduct.getSku()))
                .toList();

        if (openPositions.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded Product [{}] exist",
                    existingProduct.shortId());
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }

        var openPosition = openPositions.stream()
                //.filter(p -> p.getQuantityExpected().getUnitType().equals(quantityReceived.getUnitType()))
                .filter(p -> p.getQuantityExpected().compareTo(request.getQuantityReceived()) >= 0)
                .findFirst();

        if (openPosition.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded quantity exist");
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }

        func.accept(null);

        openPosition.get().addQuantityReceived(request.getQuantityReceived());
        if (openPosition.get().getQuantityReceived().compareTo(openPosition.get().getQuantityExpected()) >= 0) {
            openPosition.get().changePositionState(publisher, PositionState.COMPLETED);
        } else {
            openPosition.get().changePositionState(publisher, PositionState.PROCESSING);
        }
        receivingOrder = repository.save(receivingOrder);
        return Optional.of(receivingOrder);
    }

        @Override
    public boolean supports(CaptureRequestVO request) {
        return request instanceof QuantityCaptureOnLocationRequestVO;
    }
}
