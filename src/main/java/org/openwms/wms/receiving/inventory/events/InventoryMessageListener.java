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
package org.openwms.wms.receiving.inventory.events;

import org.ameba.annotation.Measured;
import org.openwms.core.SpringProfiles;
import org.openwms.wms.receiving.inventory.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * A InventoryMessageListener is a Spring managed bean, active in profile ASYNCHRONOUS that listens on Product changes.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
public class InventoryMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryMessageListener.class);
    private final ApplicationEventPublisher publisher;
    private final ProductMapper mapper;

    public InventoryMessageListener(ApplicationEventPublisher publisher, ProductMapper mapper) {
        this.publisher = publisher;
        this.mapper = mapper;
    }

    @Measured
    @RabbitListener(queues = "${owms.events.inventory.products.queue-name}")
    void handle(InventoryProductMO msg, @Header("owms_event_type") String header) {
        try {
            switch (header) {
                case "created" -> {
                    LOGGER.debug("Product has been created in Inventory service: [{}]", msg);
                    publisher.publishEvent(new ProductEvent(mapper.convertFromMO(msg), ProductEvent.TYPE.CREATED));
                }
                case "updated" -> {
                    LOGGER.debug("Product has been updated in Inventory service: [{}]", msg);
                    publisher.publishEvent(new ProductEvent(mapper.convertFromMO(msg), ProductEvent.TYPE.UPDATED));
                }
                case "deleted" -> {
                    LOGGER.debug("Product has been deleted in Inventory service: [{}]", msg);
                    publisher.publishEvent(new ProductEvent(msg.getpKey(), ProductEvent.TYPE.DELETED));
                }
                default -> LOGGER.warn("Product event of type [{}] is currently not supported", header);
            }
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
