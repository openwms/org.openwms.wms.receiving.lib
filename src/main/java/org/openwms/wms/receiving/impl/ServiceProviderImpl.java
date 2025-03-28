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

import org.ameba.annotation.TxService;
import org.ameba.i18n.Translator;
import org.openwms.wms.receiving.inventory.ProductService;

/**
 * A ServiceProviderImpl.
 *
 * @author Heiko Scherrer
 */
@TxService
class ServiceProviderImpl implements ServiceProvider {

    private final Translator translator;
    private final ProductService productService;

    ServiceProviderImpl(Translator translator, ProductService productService) {
        this.translator = translator;
        this.productService = productService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProductService getProductService() {
        return productService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Translator getTranslator() {
        return translator;
    }
}
