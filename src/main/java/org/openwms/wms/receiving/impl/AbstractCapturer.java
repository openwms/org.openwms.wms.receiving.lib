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

import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.openwms.wms.receiving.ReceivingMessages;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductService;
import org.springframework.context.ApplicationEventPublisher;

import static org.openwms.wms.receiving.ReceivingMessages.RO_NOT_FOUND_BY_PKEY;

/**
 * A AbstractCapturer.
 *
 * @author Heiko Scherrer
 */
public abstract class AbstractCapturer {

    protected final ApplicationEventPublisher publisher;
    protected final Translator translator;
    protected final Validator validator;
    protected final ReceivingOrderRepository repository;
    protected final ProductService productService;

    AbstractCapturer(ApplicationEventPublisher publisher, Translator translator, Validator validator, ReceivingOrderRepository repository,
            ProductService productService) {
        this.publisher = publisher;
        this.translator = translator;
        this.validator = validator;
        this.repository = repository;
        this.productService = productService;
    }

    protected Product getProduct(String sku) {
        return productService.findBySku(sku).orElseThrow(
                () -> new NotFoundException(
                        translator,
                        ReceivingMessages.PRODUCT_NOT_FOUND,
                        sku
                ));
    }

    protected ReceivingOrder getOrder(@NotBlank String pKey) {
        return repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(
                translator,
                RO_NOT_FOUND_BY_PKEY,
                new String[]{pKey},
                pKey
        ));
    }
}
