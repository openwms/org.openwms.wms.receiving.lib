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
package org.openwms.wms.receiving.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * A ReceivingApi.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "wms-receiving")
public interface ReceivingApi {

    /**
     * Create a new {@code ReceivingOrder}.
     *
     * @param order The ReceivingOrder representation
     */
    @PostMapping("/v1/receiving-orders")
    void createOrder(
            @RequestBody ReceivingOrderVO order
    );

    /**
     * Update an existing {@code ReceivingOrder}.
     *
     * @param pKey The persistent identifier of the ReceivingOrder to change
     * @param order The ReceivingOrder representation with the modified values
     * @return The updated instance
     */
    @PutMapping("/v1/receiving/{pKey}")
    ReceivingOrderVO changeOrder(
            @PathVariable("pKey") String pKey,
            @RequestBody ReceivingOrderVO order
    );

    /**
     * Execute the process of capturing a Goods Receipt.
     *
     * @param pKey The persistent identifier of the ReceivingOrder the capture targets on
     * @param request Required capture request data
     */
    @PostMapping("/v1/receiving-orders/{pKey}/capture")
    void captureOrder(
            @PathVariable("pKey") String pKey,
            @RequestBody CaptureRequestVO request
    );

    /**
     * Cancel an existing {@code ReceivingOrder}.
     *
     * @param pKey The persistent identifier of the ReceivingOrder
     */
    @DeleteMapping("/v1/receiving-orders/{pKey}")
    void cancelOrder(
            @PathVariable("pKey") String pKey
    );
}
