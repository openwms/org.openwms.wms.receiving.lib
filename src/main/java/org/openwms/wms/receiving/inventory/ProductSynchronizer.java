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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * A ProductSynchronizer offers methods to synchronize the internal state of a {@link Product} with an external source.
 *
 * @author Heiko Scherrer
 */
public interface ProductSynchronizer {

    /**
     * Create a new {@link Product} instance.
     *
     * @param product The instance to create
     */
    void create(@NotNull Product product);

    /**
     * Update an existing {@link Product} instance
     *
     * @param product The instance to update
     * @return The updated instance
     */
    Product update(@NotNull Product product);

    /**
     * Delete an existing {@link Product}.
     *
     * @param pKey The persistent key of the instance to delete
     */
    void delete(@NotBlank String pKey);
}
