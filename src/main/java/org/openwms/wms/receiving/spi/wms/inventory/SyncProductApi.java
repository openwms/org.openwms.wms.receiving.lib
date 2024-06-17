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
package org.openwms.wms.receiving.spi.wms.inventory;

import jakarta.validation.constraints.NotBlank;

/**
 * A SyncProductApi.
 *
 * @author Heiko Scherrer
 */
public interface SyncProductApi {

    /**
     * Find and return a {@code Product} identified by its {@code SKU}.
     *
     * @param sku The identifying SKU attribute
     * @return The instance or null
     */
    ProductVO findBySKU(@NotBlank String sku);

    /**
     * Gets {@code Product} based on {@code ProductUnit} pKey
     *
     * @param pKey The pKey of the productUnit
     * @return The instance or null
     */
    ProductVO findProductByProductUnitPkey(@NotBlank String pKey);
}
