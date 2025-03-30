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
package org.openwms.wms.receiving.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.OrderState;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.CancellationDeniedException;
import org.openwms.wms.receiving.impl.ReceivingOrder;

import java.util.List;
import java.util.Optional;

/**
 * A RestServiceFacade.
 *
 * @author Heiko Scherrer
 */
public interface RestServiceFacade<T extends CaptureRequestVO> {

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
    @NotNull Optional<ReceivingOrderVO> capture(
            @NotBlank String pKey,
            @NotNull @Valid List<T> requests);

    /**
     * Capture an unexpected receipt (aka Blind Receipt) that has no reference to a {@code ReceivingOrder}.
     * <ul>
     *     <li>Create physical {@code PackagingUnit}(s) from the received quantity</li>
     * </ul>
     *
     * @param requests Contains all the capturing information according to the process in use
     */
    void captureBlindReceipts(
            @NotNull List<T> requests
    );

    /**
     * Cancel a {@link ReceivingOrder}.
     *
     * @param pKey The synthetic persistent key
     * @throws CancellationDeniedException in case the cancellation is not allowed
     * @return The cancelled instance
     */
    @NotNull ReceivingOrderVO cancelOrder(@NotBlank String pKey);

    /**
     * Change the state of a {@link ReceivingOrder}.
     *
     * @param pKey The synthetic persistent key
     * @param state The new state
     * @throws CancellationDeniedException in case the state change is not allowed
     * @return The updated instance
     */
    @NotNull ReceivingOrderVO changeState(@NotBlank String pKey, @NotNull OrderState state);

    /**
     * Update an existing {@link ReceivingOrder} with the given data.
     *
     * @param pKey The synthetic persistent key
     * @param receivingOrder The representation to update
     * @throws org.ameba.exception.NotFoundException if not found
     * @return The updated instance
     */
    @NotNull ReceivingOrderVO update(@NotBlank String pKey, @NotNull ReceivingOrderVO receivingOrder);

    /**
     * Complete a {@link ReceivingOrder} and all positions. Satisfy quantities and set the state to {@code COMPLETED}.
     *
     * @param pKey The synthetic persistent key
     * @return The updated instance
     */
    @NotNull ReceivingOrderVO complete(@NotBlank String pKey);
}
