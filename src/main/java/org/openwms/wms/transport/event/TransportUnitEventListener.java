/*
 * Copyright 2005-2020 the original author or authors.
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
package org.openwms.wms.transport.event;

import org.ameba.annotation.Measured;
import org.ameba.mapping.BeanMapper;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.wms.transport.TransportUnit;
import org.openwms.wms.transport.TransportUnitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * A TransportUnitEventListener is a Spring managed RabbiMQ event listener that is interested in changes on
 * TransportUnits.
 *
 * @author Heiko Scherrer
 */
@Component
class TransportUnitEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitEventListener.class);
    private final TransportUnitService service;
    private final BeanMapper mapper;

    TransportUnitEventListener(TransportUnitService service, BeanMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Measured
    @RabbitListener(queues = "${owms.events.common.tu.queue-name}")
    public void handle(@Payload TransportUnitMO mo, @Header("amqp_receivedRoutingKey") String routingKey) {
        try {
            if ("tu.event.created".equals(routingKey) ||
                    routingKey.startsWith("tu.event.moved") ||
                    routingKey.startsWith("tu.event.changed")
            ) {

                service.upsert(mapper.map(mo, TransportUnit.class));
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Event of type [{}] is currently not handled", routingKey);
                }
            }
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
