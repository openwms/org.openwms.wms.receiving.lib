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

import jakarta.validation.constraints.NotNull;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.springframework.plugin.core.Plugin;

import java.util.Optional;

/**
 * A ReceivingOrderCapturer is responsible to capture receipt goods in its particular flavor.
 *
 * @author Heiko Scherrer
 * @see T The flavor or goods receipt capturing
 */
public interface ReceivingOrderCapturer<T extends CaptureRequestVO> extends Plugin<CaptureRequestVO> {

    /**
     * Capture received goods to a suitable {@code ReceivingOrderPosition}.
     *
     * @param pKey The persistent identifier of the ReceivingOrder if present it is a capturing of an {@literal Expected Receipt} otherwise
     * it is a {@literal Blind Receipt}.
     * @param request Particular capturing detail information used to perform the capturing process
     * @return The identified and updated ReceivingOrder instance, in case of {@literal Expected Receipt}s
     */
    Optional<ReceivingOrder> capture(String pKey, @NotNull T request);
}