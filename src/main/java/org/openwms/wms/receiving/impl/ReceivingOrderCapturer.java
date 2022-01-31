/*
 * Copyright 2005-2021 the original author or authors.
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

import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.springframework.plugin.core.Plugin;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * A ReceivingOrderCapturer.
 *
 * @author Heiko Scherrer
 */
public interface ReceivingOrderCapturer<T extends CaptureRequestVO> extends Plugin<T> {

    ReceivingOrder capture(@NotEmpty String pKey, @NotEmpty String loadUnitType, @NotNull T request);
}