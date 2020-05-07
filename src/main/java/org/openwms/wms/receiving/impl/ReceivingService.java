/*
 * Copyright 2005-2020 the original author or authors.
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

import org.openwms.core.units.api.Measurable;
import org.openwms.wms.inventory.Product;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * A ReceivingService managed {@link ReceivingOrder}s.
 *
 * @author Heiko Scherrer
 */
public interface ReceivingService {

    /**
     * Create a {@link ReceivingOrder} with containing {@link ReceivingOrderPosition}.
     *
     * @param order The ReceivingOrder instance to create
     * @return The saved instance
     */
    ReceivingOrder createOrder(@NotNull ReceivingOrder order);

    /**
     * Decrease one of the {@code ReceivingOrderPosition}s by the received material with the given quantity of the given {@code Product} and
     * book the received physical article to the {@code TransportUnit} identified by the {@code transportUnitId}.
     *
     * @param pKey The persistent key of the ReceivingOrder
     * @param transportUnitId The identifier of the TransportUnit to book the Product to
     * @param quantityReceived The received quantity
     * @param product The received Product
     * @return The updated ReceivingOrder instance with updated positions
     */
    ReceivingOrder capture(@NotEmpty String pKey, @NotEmpty String transportUnitId, @NotNull Measurable<?, ?, ?> quantityReceived,
            @NotNull Product product);

    /**
     * Cancel a {@link ReceivingOrder}.
     *
     * @param pKey The synthetic persistent key
     * @throws CancellationDeniedException in case the cancellation is not allowed
     */
    void cancelOrder(@NotEmpty String pKey);

    /**
     * Find and return all existing {@link ReceivingOrder}s.
     *
     * @return A list of ReceivingOrders, never {@literal null}
     */
    List<ReceivingOrder> findAll();

    /**
     * Find and return a {@link ReceivingOrder} identified by its synthetic persistent key.
     *
     * @param pKey The synthetic persistent key
     * @return The instance
     * @throws org.ameba.exception.NotFoundException if not found
     */
    ReceivingOrder findByPKey(@NotEmpty String pKey);

    /**
     * Find and return a {@link ReceivingOrder} identified by its business key.
     *
     * @param orderId The business key
     * @return The order instance
     */
    Optional<ReceivingOrder> findByOrderId(@NotEmpty String orderId);
}