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
import org.ameba.i18n.Translator;
import org.ameba.system.ValidationUtil;
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.PositionState;
import org.openwms.wms.receiving.api.TUCaptureRequestVO;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.location.LocationVO;
import org.openwms.wms.receiving.spi.wms.location.SyncLocationApi;
import org.openwms.wms.receiving.spi.wms.receiving.CapturingApproval;
import org.openwms.wms.receiving.spi.wms.transport.SyncTransportUnitApi;
import org.openwms.wms.receiving.spi.wms.transport.TransportUnitVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS_TU;
import static org.openwms.wms.receiving.ReceivingMessages.TU_TYPE_NOT_GIVEN;

/**
 * A TUCaptureRequestCapturer.
 *
 * @author Heiko Scherrer
 */
@TxService
class TUCaptureRequestCapturer extends AbstractCapturer<TUCaptureRequestVO> implements ReceivingOrderCapturer<TUCaptureRequestVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TUCaptureRequestCapturer.class);
    private final SyncTransportUnitApi transportUnitApi;
    private final SyncLocationApi locationApi;

    TUCaptureRequestCapturer(ApplicationEventPublisher publisher, Translator translator, Validator validator,
                             ReceivingOrderRepository repository,
                             @Autowired(required = false) List<CapturingApproval<TUCaptureRequestVO>> capturingApprovals,
                             ProductService productService, SyncTransportUnitApi transportUnitApi,
                             SyncLocationApi locationApi) {
        super(publisher, translator, validator, repository, capturingApprovals, productService);
        this.transportUnitApi = transportUnitApi;
        this.locationApi = locationApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<ReceivingOrder> capture(String pKey, @NotNull TUCaptureRequestVO request) {
        if (pKey != null) {
            ValidationUtil.validate(validator, request, ValidationGroups.CreateExpectedTUReceipt.class);
            return handleExpectedReceipt(
                    pKey,
                    request);
        }
        ValidationUtil.validate(validator, request, ValidationGroups.CreateBlindTUReceipt.class);
        if (!request.hasTransportUnitType()) {
            throw new CapturingException(translator, TU_TYPE_NOT_GIVEN, new String[0]);
        }
        var locationOpt = locationApi.findByErpCodeOpt(request.getActualLocation().getErpCode());
        var location = locationOpt.map(locationVO -> LocationVO.of(locationVO.getLocationId())).orElseGet(LocationVO::new); // Handle Locations without locationId later
        location.setErpCode(request.getActualLocation().getErpCode());
        var tu = new TransportUnitVO(request.getTransportUnit().getTransportUnitId(), location, request.getTransportUnit().getTransportUnitType());
        transportUnitApi.createTU(tu);
        return Optional.empty();
    }

    private Optional<ReceivingOrder> handleExpectedReceipt(String pKey, TUCaptureRequestVO request) {
        var receivingOrder = getOrder(pKey);
        receivingOrder.getPositions().forEach(p -> capturingApprovals.forEach(ca -> ca.approve(p, request)));
        final var transportUnitBK = request.getTransportUnit().getTransportUnitId();
        final var actualLocationErpCode = request.getActualLocation().getErpCode();
        var openPosition = receivingOrder.getPositions().stream()
                .filter(p -> p.getState() == PositionState.CREATED || p.getState() == PositionState.PROCESSING)
                .filter(ReceivingTransportUnitOrderPosition.class::isInstance)
                .map(ReceivingTransportUnitOrderPosition.class::cast)
                .filter(p -> p.getTransportUnitBK().equals(transportUnitBK))
                .findFirst();

        if (openPosition.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingTransportUnitOrderPosition with the expected TransportUnit exist");
            throw new CapturingException(translator, RO_NO_OPEN_POSITIONS_TU, new String[0]);
        }
        LOGGER.info("Received TransportUnit [{}] with ReceivingOrder [{}] and ReceivingOrderPosition [{}]",
                transportUnitBK, receivingOrder.getOrderId(), openPosition.get().getPosNo());
        openPosition.get().changePositionState(publisher, PositionState.COMPLETED);
        receivingOrder = repository.save(receivingOrder);
        transportUnitApi.moveTU(transportUnitBK, actualLocationErpCode);
        return Optional.of(receivingOrder);
    }

    @Override
    public boolean supports(CaptureRequestVO request) {
        return request instanceof TUCaptureRequestVO;
    }
}
