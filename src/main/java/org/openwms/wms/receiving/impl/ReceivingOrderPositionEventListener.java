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

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * A ReceivingOrderPositionEventListener listens on events according to {@link ReceivingOrderPosition}s.
 *
 * @author Heiko Scherrer
 */
@TxService
class ReceivingOrderPositionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivingOrderPositionEventListener.class);
    private final ApplicationEventPublisher publisher;
    private final ReceivingOrderRepository repository;

    ReceivingOrderPositionEventListener(ApplicationEventPublisher publisher, ReceivingOrderRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    /**
     * Change state of a {@link ReceivingOrder} after a position has changed its state.
     *
     * @param event Expected to store the position as source
     */
    @Measured
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {IllegalArgumentException.class})
    public void onStateChange(ReceivingOrderPositionStateChangeEvent event) {
        var position = event.getSource();
        var order = repository.findBypKey(position.getOrder().getPersistentKey()).orElseThrow();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("State of ReceivingOrderPosition has changed, recalculating the state of ReceivingOrder [{}]", order.getOrderId());
        }
        if (order.recalculateOrderState(publisher)) {
            LOGGER.info("State of ReceivingOrder [{}] changed to [{}]", order.getOrderId(), order.getOrderState());
            repository.save(order);
        }
    }
}
