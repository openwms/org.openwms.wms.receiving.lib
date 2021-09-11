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

import org.ameba.annotation.Measured;
import org.ameba.mapping.BeanMapper;
import org.openwms.core.SpringProfiles;
import org.openwms.wms.receiving.api.events.ProductMO;
import org.openwms.wms.receiving.inventory.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * A InventoryEventListener is a Spring managed bean, active in profile ASYNCHRONOUS that listens for Product changes.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
public class InventoryEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryEventListener.class);
    private final ApplicationEventPublisher publisher;
    private final BeanMapper mapper;

    public InventoryEventListener(ApplicationEventPublisher publisher, BeanMapper mapper) {
        this.publisher = publisher;
        this.mapper = mapper;
    }

    @Measured
    @RabbitListener(queues = "${owms.events.inventory.products.queue-name}")
    void handle(ProductMO msg, @Header("owms_event_type") String header) {
        try {
            switch(header) {
                case "created":
                    LOGGER.debug("Product has been created in Inventory service");
                    publisher.publishEvent(new ProductEvent(mapper.map(msg, Product.class), ProductEvent.TYPE.CREATED));
                    break;
                case "updated":
                    LOGGER.debug("Product has been updated in Inventory service");
                    publisher.publishEvent(new ProductEvent(mapper.map(msg, Product.class), ProductEvent.TYPE.UPDATED));
                    break;
                case "deleted":
                    LOGGER.debug("Product has been deleted in Inventory service: [{}]", msg.toString());
                    publisher.publishEvent(new ProductEvent(msg.getpKey(), ProductEvent.TYPE.DELETED));
                    break;
                default:
                    LOGGER.warn("Event of type [{}] is currently not supported", header);
            }
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }
}
