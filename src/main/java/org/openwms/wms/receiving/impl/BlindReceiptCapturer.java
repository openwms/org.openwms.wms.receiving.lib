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
package org.openwms.wms.receiving.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.springframework.plugin.core.Plugin;

/**
 * A BlindReceiptCapturer is able to actually captures unexpected material (Blind Receipts) at Goods-In.
 *
 * @author Heiko Scherrer
 * @see T The flavor or goods receipt capturing
 */
public interface BlindReceiptCapturer<T extends CaptureRequestVO> extends Plugin<CaptureRequestVO> {

    /**
     * Capture received goods without a reference to a {@code ReceivingOrderPosition}.
     *
     * @param request Particular capturing detail information used to perform the capturing process
     */
    @NotNull void capture(@Valid @NotNull T request);
}