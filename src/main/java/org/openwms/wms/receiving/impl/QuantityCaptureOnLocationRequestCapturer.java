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
import org.openwms.wms.receiving.api.LocationVO;
import org.openwms.wms.receiving.api.QuantityCaptureOnLocationRequestVO;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.inventory.PackagingUnitApi;
import org.openwms.wms.receiving.spi.wms.inventory.PackagingUnitVO;
import org.openwms.wms.receiving.spi.wms.inventory.ProductApi;
import org.openwms.wms.receiving.spi.wms.inventory.ProductVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.openwms.wms.order.OrderState.CREATED;
import static org.openwms.wms.order.OrderState.PROCESSING;
import static org.openwms.wms.receiving.ReceivingMessages.PRODUCT_NOT_FOUND;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS;

/**
 * A QuantityCaptureOnLocationRequestCapturer.
 *
 * @author Heiko Scherrer
 */
@TxService
class QuantityCaptureOnLocationRequestCapturer extends AbstractCapturer implements ReceivingOrderCapturer<QuantityCaptureOnLocationRequestVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantityCaptureOnLocationRequestCapturer.class);
    private final PackagingUnitApi packagingUnitApi;
    private final ProductService productService;
    private final ProductApi productApi;

    QuantityCaptureOnLocationRequestCapturer(Translator translator, ReceivingOrderRepository repository, ProductService productService,
            PackagingUnitApi packagingUnitApi, ProductService productService1, ProductApi productApi) {
        super(translator, repository, productService);
        this.packagingUnitApi = packagingUnitApi;
        this.productService = productService1;
        this.productApi = productApi;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public @NotNull ReceivingOrder capture(@NotBlank String pKey, @Valid @NotNull QuantityCaptureOnLocationRequestVO request) {
        final var existingProduct = request.hasUomRelation()
                ? productApi.findProductByProductUnitPkey(request.getUomRelation().pKey).getSku()
                : productService.findBySku(request.getProduct().getSku())
                .orElseThrow(() -> new CapturingException(translator, PRODUCT_NOT_FOUND, new String[]{request.getProduct().getSku()})).getSku();
        if (existingProduct == null) {
            throw new CapturingException(translator, PRODUCT_NOT_FOUND, new String[]{request.getProduct().getSku()});
        }
        final var quantityReceived = request.getQuantityReceived();
        var receivingOrder = getOrder(pKey);
        final var erpCode = request.getActualLocation().getErpCode();
        final var details = request.getDetails();

        var openPositions = receivingOrder.getPositions().stream()
                .filter(p -> p.getState() == CREATED || p.getState() == PROCESSING)
                .filter(ReceivingOrderPosition.class::isInstance)
                .map(ReceivingOrderPosition.class::cast)
                .filter(p -> p.getProduct().getSku().equals(existingProduct))
                .toList();

        if (openPositions.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded Product exist");
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }

        Optional<ReceivingOrderPosition> openPosition = openPositions.stream()
                .filter(p -> p.getQuantityExpected().getUnit().equals(quantityReceived.getUnit()))
                .filter(p -> p.getQuantityExpected().getValue().doubleValue() >= quantityReceived.getValue().doubleValue())
                .findFirst();

        if (openPosition.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded quantity exist");
            throw new ProcessingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }

        // multi packs
        var pu = request.hasUomRelation()
                ? new PackagingUnitVO(request.getUomRelation(), quantityReceived)
                : new PackagingUnitVO(ProductVO.newBuilder().sku(request.getProduct().getSku()).build(), quantityReceived);
        pu.setActualLocation(new LocationVO(erpCode));
        pu.setDetails(details);
        pu.setSerialNumber(request.getSerialNumber());
        pu.setLotId(request.getLotId());
        pu.setProduct(ProductVO.newBuilder().sku(existingProduct).build());
        pu.setExpiresAt(request.getExpiresAt());
        pu.setProductionDate(request.getProductionDate());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Create new PackagingUnit [{}] on Location [{}]", pu, erpCode);
        }
        packagingUnitApi.createOnLocation(pu);
        openPosition.get().addQuantityReceived(quantityReceived);
        receivingOrder = repository.save(receivingOrder);
        return receivingOrder;
    }

    @Override
    public boolean supports(CaptureRequestVO request) {
        return request instanceof QuantityCaptureOnLocationRequestVO;
    }
}
