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
package org.openwms.wms.receiving.events;

import org.ameba.annotation.Measured;
import org.ameba.app.SpringProfiles;
import org.openwms.wms.receiving.CycleAvoidingMappingContext;
import org.openwms.wms.receiving.api.events.ReceivingOrderPositionStateChangeEvent;
import org.openwms.wms.receiving.api.events.ReceivingOrderStateChangeEvent;
import org.openwms.wms.receiving.impl.AbstractReceivingOrderPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * A EventPropagator is a Spring managed bean, actived in profile ASYNCHRONOUS that propagates internal events to the outer world via AMQP.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
public class EventPropagator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPropagator.class);
    private final AmqpTemplate amqpTemplate;
    private final ReceivingMOMapper mapper;
    private final String receivingExchangeName;

    public EventPropagator(AmqpTemplate amqpTemplate,
            ReceivingMOMapper mapper, @Value("${owms.events.receiving.exchange-name}") String receivingExchangeName) {
        this.amqpTemplate = amqpTemplate;
        this.mapper = mapper;
        this.receivingExchangeName = receivingExchangeName;
    }

    @Measured
    @TransactionalEventListener
    public void onEvent(ReceivingOrderStateChangeEvent event) {
        var mo = mapper.convertToMO(event.getSource(), new CycleAvoidingMappingContext());
        switch (event.getState()) {
            case COMPLETED -> {
                LOGGER.debug("ReceivingOrder [{}] with all positions completed, sending ReceivingOrderMO: [{}]",
                        event.getSource().getPersistentKey(), mo);
                amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.ro.completed", mo);
            }
            case PARTIALLY_COMPLETED -> {
                LOGGER.debug("ReceivingOrder [{}] partially completed, sending ReceivingOrderMO: [{}]",
                        event.getSource().getPersistentKey(), mo);
                amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.ro.partially-completed", mo);
            }
            case CANCELED -> {
                LOGGER.debug("ReceivingOrder [{}] with all positions cancelled, sending ReceivingOrderMO: [{}]",
                        event.getSource().getPersistentKey(), mo);
                amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.ro.cancelled", mo);
            }
            default -> LOGGER.debug("ReceivingOrderStateChangeEvent [{}] not exposed", event.getState());
        }
    }

    @Measured
    @TransactionalEventListener
    public <T extends AbstractReceivingOrderPosition> void onEvent(ReceivingOrderPositionStateChangeEvent<T> event) {
        var mo = mapper.fromEOtoMO(event.getSource(), new CycleAvoidingMappingContext());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ReceivingOrderPosition [{}]/[{}] changed state to [{}], sending ReceivingOrderPositionMO [{}]",
                    event.getSource().getOrder().getOrderId(), event.getSource().getPosNo(), event.getState(), mo);
        }
        switch(event.getState()) {
            case CREATED -> amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.rop.created", mo);
            case PROCESSING -> amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.rop.processing", mo);
            case CANCELED -> amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.rop.canceled", mo);
            case PARTIALLY_COMPLETED -> amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.rop.partially_completed", mo);
            case COMPLETED -> amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.rop.completed", mo);
            default -> LOGGER.warn("ReceivingOrderPositionStateChangeEvent [{}] not supported", event.getState());
        }
    }
}
