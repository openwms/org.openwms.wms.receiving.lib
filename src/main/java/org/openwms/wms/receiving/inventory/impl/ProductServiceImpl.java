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

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductService;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * A ProductServiceImpl.
 *
 * @author Heiko Scherrer
 */
@TxService
class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<Product> findBySku(@NotEmpty String sku) {
        return repository.findBySku(sku);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void create(@NotNull Product product) {
        repository.save(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Product update(@NotNull Product product) {
        return repository.save(product);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void delete(@NotEmpty String pKey) {
        repository.findBypKey(pKey).ifPresent(repository::delete);
    }
}
