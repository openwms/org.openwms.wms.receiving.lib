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
import org.openwms.wms.receiving.api.OrderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * A ReceivingOrderEventListener listens on events according to {@link ReceivingOrder}s.
 *
 * @author Heiko Scherrer
 */
@TxService
class ReceivingOrderEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivingOrderEventListener.class);
    private final OrderPositionProcessor positionProcessor;
    private final ReceivingOrderRepository repository;

    ReceivingOrderEventListener(
            @Autowired(required = false) OrderPositionProcessor positionProcessor,
            ReceivingOrderRepository repository
    ) {
        this.positionProcessor = positionProcessor;
        this.repository = repository;
    }

    /**
     * After a {@link ReceivingOrder} is created all positions are validated.
     *
     * @param event Expected to keep the created order instance
     */
    @Measured
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {IllegalArgumentException.class})
    public void onCreate(ReceivingOrderCreatedEvent event) {
        var order = event.getSource();
        LOGGER.info("ReceivingOrder with orderId [{}] created", order.getOrderId());
        LOGGER.debug("Validating ReceivingOrder [{}]", order.getOrderId());
        order.setOrderState(OrderState.VALIDATED);
        if (positionProcessor != null) {
            order.getPositions().forEach(p -> positionProcessor.processPosition(order, p));
        }
        repository.save(order);
    }
}
