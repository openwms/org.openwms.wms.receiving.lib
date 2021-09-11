/*
 * Copyright 2005-2021 the original author or authors.
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

import org.ameba.app.SpringProfiles;
import org.ameba.mapping.BeanMapper;
import org.openwms.wms.receiving.api.events.ReceivingOrderCompletedEvent;
import org.openwms.wms.receiving.api.events.ReceivingOrderMO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
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
    private final BeanMapper mapper;
    private final String receivingExchangeName;

    public EventPropagator(AmqpTemplate amqpTemplate,
            BeanMapper mapper, @Value("${owms.events.receiving.exchange-name}") String receivingExchangeName) {
        this.amqpTemplate = amqpTemplate;
        this.mapper = mapper;
        this.receivingExchangeName = receivingExchangeName;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEvent(ReceivingOrderCompletedEvent event) {
        ReceivingOrderMO mo = mapper.map(event.getSource(), ReceivingOrderMO.class);
        LOGGER.debug("Sending out ReceivingOrderMO: [{}]", mo);
        amqpTemplate.convertAndSend(receivingExchangeName, "receiving.event.ro.created", mo);
        LOGGER.debug("ReceivingOrder [{}] with all positions completed", event.getSource().getPersistentKey());
    }
}
