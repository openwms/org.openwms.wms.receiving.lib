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
import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.ameba.system.ValidationUtil;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.LocationVO;
import org.openwms.wms.receiving.api.QuantityCaptureOnLocationRequestVO;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.inventory.PackagingUnitVO;
import org.openwms.wms.receiving.spi.wms.inventory.ProductVO;
import org.openwms.wms.receiving.spi.wms.inventory.SyncPackagingUnitApi;
import org.openwms.wms.receiving.spi.wms.inventory.SyncProductApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.openwms.wms.order.OrderState.CREATED;
import static org.openwms.wms.order.OrderState.PROCESSING;
import static org.openwms.wms.receiving.ReceivingMessages.PRODUCT_NOT_FOUND;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS;

/**
 * A QuantityCaptureOnLocationRequestCapturer accepts capturing inbound goods on a Location.
 *
 * @author Heiko Scherrer
 */
@TxService
class QuantityCaptureOnLocationRequestCapturer extends AbstractCapturer implements ReceivingOrderCapturer<QuantityCaptureOnLocationRequestVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityCaptureOnLocationRequestCapturer.class);
    private final Validator validator;
    private final SyncPackagingUnitApi packagingUnitApi;
    private final SyncProductApi productApi;

    QuantityCaptureOnLocationRequestCapturer(Translator translator, ReceivingOrderRepository repository, ProductService productService,
            Validator validator, SyncPackagingUnitApi packagingUnitApi, SyncProductApi productApi) {
        super(translator, repository, productService);
        this.validator = validator;
        this.packagingUnitApi = packagingUnitApi;
        this.productApi = productApi;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public Optional<ReceivingOrder> capture(String pKey, @NotNull QuantityCaptureOnLocationRequestVO request) {
        var product = getProduct(request);
        if (pKey != null) {
            ValidationUtil.validate(validator, request, ValidationGroups.CreateQuantityReceipt.class);
            return handleExpectedReceipt(
                    pKey,
                    request.getQuantityReceived(),
                    product,
                    v -> createPackagingUnitsForDemand(request, product)
            );
        } else {
            ValidationUtil.validate(validator, request, ValidationGroups.CreateQuantityReceipt.class);
            createPackagingUnitsForDemand(request, product);
            return Optional.empty();
        }
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

    private void createPackagingUnitsForDemand(QuantityCaptureOnLocationRequestVO request, Product product) {
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
        pu.setProduct(ProductVO.newBuilder().sku(product.getSku()).build());
        pu.setExpiresAt(request.getExpiresAt());
        pu.setProductionDate(request.getProductionDate());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Create new PackagingUnit [{}] on Location [{}]", pu, erpCode);
        }
        packagingUnitApi.createOnLocation(pu);
    }

    private Optional<ReceivingOrder> handleExpectedReceipt(String pKey, Measurable quantityReceived, Product existingProduct,
            Consumer<Void> func) {
        var receivingOrder = getOrder(pKey);
        var openPositions = receivingOrder.getPositions().stream()
                .filter(p -> p.getState() == CREATED || p.getState() == PROCESSING)
                .filter(ReceivingOrderPosition.class::isInstance)
                .map(ReceivingOrderPosition.class::cast)
                .filter(p -> p.getProduct().getSku().equals(existingProduct.getSku()))
                .toList();

        if (openPositions.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded Product exist");
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }

        var openPosition = openPositions.stream()
                .filter(p -> p.getQuantityExpected().getUnitType().equals(quantityReceived.getUnitType()))
                .filter(p -> p.getQuantityExpected().compareTo(quantityReceived) >= 0)
                .findFirst();

        if (openPosition.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded quantity exist");
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }

        func.accept(null);

        openPosition.get().addQuantityReceived(quantityReceived);
        receivingOrder = repository.save(receivingOrder);
        return Optional.of(receivingOrder);
    }

        @Override
    public boolean supports(CaptureRequestVO request) {
        return request instanceof QuantityCaptureOnLocationRequestVO;
    }
}
