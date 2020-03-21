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
import org.openwms.wms.transport.TransportUnit;
import org.openwms.wms.transport.TransportUnitService;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * A TransportUnitEventListener.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class TransportUnitEventListener {

    private final TransportUnitService service;
    private final BeanMapper mapper;

    TransportUnitEventListener(TransportUnitService service, BeanMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Measured
    @RabbitListener(queues = "${owms.receiving.orders.queue-name}")
    public void handle(@Payload TransportUnitMO mo) {
        try {
            service.upsert(mapper.map(mo, TransportUnit.class));
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
