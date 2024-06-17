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
package org.openwms.wms.receiving.impl.updaters;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingOrderUpdater;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * A ReceivingOrderDetailsUpdater is a {@link ReceivingOrderUpdater} strategy.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class ReceivingOrderDetailsUpdater implements ReceivingOrderUpdater {

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @Measured
    public @NotNull ReceivingOrder update(@NotNull ReceivingOrder existingReceivingOrder, @Valid @NotNull ReceivingOrder receivingOrder) {
        existingReceivingOrder.getDetails().putAll(receivingOrder.getDetails());
        return existingReceivingOrder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Type delimiter) {
        return delimiter == Type.DETAILS_CHANGE;
    }
}
