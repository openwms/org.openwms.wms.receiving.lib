/*
 * Copyright 2005-2022 the original author or authors.
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

import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * A ReceivingService manages {@link ReceivingOrder}s.
 *
 * @author Heiko Scherrer
 */
public interface ReceivingService<T extends CaptureRequestVO> {

    /**
     * Create a {@link ReceivingOrder} with containing {@link ReceivingOrderPosition}.
     *
     * @param order The ReceivingOrder instance to create
     * @return The saved instance
     */
    @NotNull ReceivingOrder createOrder(@NotNull ReceivingOrder order);

    /**
     * Capturing on a {@code ReceivingOrder} means:
     * <ul>
     *     <li>Decrease one of the {@code ReceivingOrderPosition}s by the received amount of the given {@code Product}</li>
     *     <li>Create physical {@code PackagingUnit}(s) from the received quantity</li>
     * </ul>
     *
     * @param pKey The persistent key of the ReceivingOrder
     * @param requests Contains all the capturing information according to the process in use
     * @return The updated ReceivingOrder instance with updated positions
     */
    @NotNull ReceivingOrderVO capture(
            @NotBlank String pKey,
            @NotNull @Valid List<T> requests
    );

    /**
     * Cancel a {@link ReceivingOrder}.
     *
     * @param pKey The synthetic persistent key
     * @throws CancellationDeniedException in case the cancellation is not allowed
     * @return The cancelled instance
     */
    @NotNull ReceivingOrder cancelOrder(@NotBlank String pKey);

    /**
     * Change the state of a {@link ReceivingOrder}.
     *
     * @param pKey The synthetic persistent key
     * @param state The new state
     * @throws CancellationDeniedException in case the state change is not allowed
     * @return The updated instance
     */
    @NotNull ReceivingOrder changeState(@NotBlank String pKey, @NotNull OrderState state);

    /**
     * Find and return all existing {@link ReceivingOrder}s.
     *
     * @return A list of ReceivingOrders, never {@literal null}
     */
    @NotNull List<ReceivingOrder> findAll();

    /**
     * Find and return a {@link ReceivingOrder} identified by its synthetic persistent key.
     *
     * @param pKey The synthetic persistent key
     * @return The instance
     * @throws org.ameba.exception.NotFoundException if not found
     */
    @NotNull ReceivingOrder findByPKey(@NotBlank String pKey);

    /**
     * Find and return a {@link ReceivingOrder} identified by its business key.
     *
     * @param orderId The business key
     * @return The order instance
     */
    Optional<ReceivingOrder> findByOrderId(@NotBlank String orderId);

    /**
     * Update an existing {@link ReceivingOrder} with the given data.
     *
     * @param pKey The synthetic persistent key
     * @param receivingOrder The representation to update
     * @throws org.ameba.exception.NotFoundException if not found
     * @return The updated instance
     */
    @NotNull ReceivingOrder update(@NotBlank String pKey, @NotNull ReceivingOrder receivingOrder);

    /**
     * Complete a {@link ReceivingOrder} and all positions. Satisfy quantities and set the state to {@code COMPLETED}.
     *
     * @param pKey The synthetic persistent key
     * @return The updated instance
     */
    @NotNull ReceivingOrderVO complete(@NotBlank String pKey);
}