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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.plugin.core.Plugin;

/**
 * A ReceivingOrderUpdater.
 *
 * @author Heiko Scherrer
 */
public interface ReceivingOrderUpdater extends Plugin<ReceivingOrderUpdater.Type> {

    enum Type {
        DETAILS_CHANGE
    }

    /**
     * Update parts of a {@link ReceivingOrder} instance.
     *
     * @param existingReceivingOrder The existing unmodified instance
     * @param receivingOrder The instance with modified values to update
     * @return The updated (not saved) instance
     */
    @NotNull ReceivingOrder update(@NotNull ReceivingOrder existingReceivingOrder,
                          @Valid @NotNull ReceivingOrder receivingOrder);
}