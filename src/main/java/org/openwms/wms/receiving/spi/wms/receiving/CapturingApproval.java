/*
 * Copyright 2005-2024 the original author or authors.
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
package org.openwms.wms.receiving.spi.wms.receiving;

import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.impl.AbstractReceivingOrderPosition;

/**
 * A CapturingApproval.
 *
 * @param <T> Some subclass of CaptureRequestVO
 * @author Heiko Scherrer
 */
@FunctionalInterface
public interface CapturingApproval<T extends CaptureRequestVO> {

    /**
     * Approve that the requested capture of the given {@code receivingOrderPosition} is allowed.
     *
     * @param receivingOrderPosition The ReceivingOrderPosition to validate and approve
     * @param request The request information that is available for validation
     * @throws NotApprovedException If not allowed to capture order
     */
    void approve(AbstractReceivingOrderPosition receivingOrderPosition, T request);
}
