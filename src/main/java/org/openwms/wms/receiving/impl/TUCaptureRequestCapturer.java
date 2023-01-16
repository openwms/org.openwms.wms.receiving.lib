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
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.TUCaptureRequestVO;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.transport.SyncTransportUnitApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;

import static org.openwms.wms.order.OrderState.COMPLETED;
import static org.openwms.wms.order.OrderState.CREATED;
import static org.openwms.wms.order.OrderState.PROCESSING;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS_TU;

/**
 * A TUCaptureRequestCapturer.
 *
 * @author Heiko Scherrer
 */
@TxService
class TUCaptureRequestCapturer extends AbstractCapturer implements ReceivingOrderCapturer<TUCaptureRequestVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TUCaptureRequestCapturer.class);
    private final ApplicationEventPublisher publisher;
    private final SyncTransportUnitApi transportUnitApi;

    TUCaptureRequestCapturer(Translator translator, ReceivingOrderRepository repository, ProductService productService,
            ApplicationEventPublisher publisher, SyncTransportUnitApi transportUnitApi) {
        super(translator, repository, productService);
        this.publisher = publisher;
        this.transportUnitApi = transportUnitApi;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public @NotNull ReceivingOrder capture(@NotBlank String pKey, @Valid @NotNull TUCaptureRequestVO request) {
        final var transportUnitBK = request.getTransportUnitId();
        final var actualLocationErpCode = request.getActualLocationErpCode();

        var receivingOrder = getOrder(pKey);
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
        return receivingOrder;
    }

    @Override
    public boolean supports(CaptureRequestVO request) {
        return request instanceof TUCaptureRequestVO;
    }
}
