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
package org.openwms.wms.receiving.inventory.impl;

import org.openwms.wms.receiving.events.ProductEvent;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A ProductEventListener.
 *
 * @author Heiko Scherrer
 */
@Component
public class ProductEventListener {

    private final ProductService productService;

    public ProductEventListener(ProductService productService) {
        this.productService = productService;
    }

    @EventListener
    public void onEvent(ProductEvent event) {
        switch (event.getType()) {
            case CREATED -> productService.create((Product) event.getSource());
            case UPDATED -> productService.update((Product) event.getSource());
            case DELETED -> productService.delete((String) event.getSource());
            default -> {}
        }
    }
}
