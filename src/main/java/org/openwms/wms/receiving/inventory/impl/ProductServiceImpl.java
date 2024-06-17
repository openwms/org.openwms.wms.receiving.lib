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

import jakarta.validation.constraints.NotBlank;
import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductMapper;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.spi.wms.inventory.SyncProductApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * A ProductServiceImpl.
 *
 * @author Heiko Scherrer
 */
@TxService
class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductMapper mapper;
    private final ProductRepository repository;
    private final SyncProductApi productApi;

    ProductServiceImpl(ProductMapper mapper, ProductRepository repository, SyncProductApi productApi) {
        this.mapper = mapper;
        this.repository = repository;
        this.productApi = productApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<Product> findBySku(@NotBlank String sku) {
        var vo = productApi.findBySKU(sku);
        if (vo == null) {
            LOGGER.debug("Getting the Product with [{}] from the database instead of the Inventory Service", sku);
            return repository.findBySku(sku);
        }
        var savedOne = repository.findByForeignPKey(vo.getpKey());
        if (savedOne.isPresent()) {
            return savedOne;
        }
        return Optional.of(repository.save(mapper.convertFromVO(vo)));
    }
}
