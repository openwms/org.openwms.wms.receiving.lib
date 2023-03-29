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
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.TUCaptureRequestVO;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.location.LocationVO;
import org.openwms.wms.receiving.spi.wms.location.SyncLocationApi;
import org.openwms.wms.receiving.spi.wms.transport.SyncTransportUnitApi;
import org.openwms.wms.receiving.spi.wms.transport.TransportUnitVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.openwms.wms.order.OrderState.COMPLETED;
import static org.openwms.wms.order.OrderState.CREATED;
import static org.openwms.wms.order.OrderState.PROCESSING;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS_TU;
import static org.openwms.wms.receiving.ReceivingMessages.TU_TYPE_NOT_GIVEN;

/**
 * A TUCaptureRequestCapturer.
 *
 * @author Heiko Scherrer
 */
@TxService
class TUCaptureRequestCapturer extends AbstractCapturer implements ReceivingOrderCapturer<TUCaptureRequestVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TUCaptureRequestCapturer.class);
    private final Validator validator;
    private final ApplicationEventPublisher publisher;
    private final SyncTransportUnitApi transportUnitApi;
    private final SyncLocationApi locationApi;

    TUCaptureRequestCapturer(Translator translator, ReceivingOrderRepository repository, ProductService productService,
            Validator validator, ApplicationEventPublisher publisher, SyncTransportUnitApi transportUnitApi, SyncLocationApi locationApi) {
        super(translator, repository, productService);
        this.validator = validator;
        this.publisher = publisher;
        this.transportUnitApi = transportUnitApi;
        this.locationApi = locationApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<ReceivingOrder> capture(Optional<String> pKey, @NotNull TUCaptureRequestVO request) {
        if (pKey.isPresent()) {
            ValidationUtil.validate(validator, request, ValidationGroups.CreateExpectedTUReceipt.class);
            return handleExpectedReceipt(
                    pKey.get(),
                    request);
        }
        ValidationUtil.validate(validator, request, ValidationGroups.CreateBlindTUReceipt.class);
        if (!request.hasTransportUnitType()) {
            throw new CapturingException(translator, TU_TYPE_NOT_GIVEN, new String[0]);
        }
        var locationOpt = locationApi.findByErpCodeOpt(request.getActualLocation().getErpCode());
        var location = locationOpt.isPresent()
                ? LocationVO.of(locationOpt.get().getLocationId())
                : new LocationVO(); // Handle Locations without locationId later
        location.setErpCode(request.getActualLocation().getErpCode());
        var tu = new TransportUnitVO(request.getTransportUnit().getTransportUnitId(), location, request.getTransportUnit().getTransportUnitType());
        transportUnitApi.createTU(tu);
        return Optional.empty();
    }

    private Optional<ReceivingOrder> handleExpectedReceipt(String pKey, TUCaptureRequestVO request) {
        var receivingOrder = getOrder(pKey);
        final var transportUnitBK = request.getTransportUnit().getTransportUnitId();
        final var actualLocationErpCode = request.getActualLocation().getErpCode();
        Optional<ReceivingTransportUnitOrderPosition> openPosition = receivingOrder.getPositions().stream()
                .filter(p -> p.getState() == CREATED || p.getState() == PROCESSING)
                .filter(ReceivingTransportUnitOrderPosition.class::isInstance)
                .map(ReceivingTransportUnitOrderPosition.class::cast)
                .filter(p -> p.getTransportUnitBK().equals(transportUnitBK))
                .findFirst();

        if (openPosition.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingTransportUnitOrderPosition with the expected TransportUnit exist");
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS_TU, new String[0]);
        }
        LOGGER.info("Received TransportUnit [{}] with ReceivingOrder [{}] in ReceivingOrderPosition [{}]",
                transportUnitBK, receivingOrder.getOrderId(), openPosition.get().getPosNo());
        openPosition.get().changeOrderState(publisher, COMPLETED);
        if (receivingOrder.getPositions().stream().allMatch(rop -> rop.getState() == COMPLETED)) {
            receivingOrder.changeOrderState(publisher, COMPLETED);
        }
        receivingOrder = repository.save(receivingOrder);
        transportUnitApi.moveTU(transportUnitBK, actualLocationErpCode);
        return Optional.of(receivingOrder);
    }

    @Override
    public boolean supports(CaptureRequestVO request) {
        return request instanceof TUCaptureRequestVO;
    }
}
