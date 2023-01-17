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
package org.openwms.wms.receiving.inventory;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * A ProductService.
 *
 * @author Heiko Scherrer
 */
public interface ProductService {

    /**
     * Find and return a {@code Product}.
     *
     * @param sku The identifying SKU
     * @return The instance
     */
    Optional<Product> findBySku(@NotEmpty String sku);

    void create(@NotNull Product product);

    Product update(@NotNull Product product);

    void delete(@NotEmpty String pKey);
}
