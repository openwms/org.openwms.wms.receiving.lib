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
@FeignClient(name = "wms-receiving", qualifier = "receivingApi")
public interface ReceivingApi {

    @PostMapping("/v1/receiving-orders")
    void createOrder(
            @RequestBody ReceivingOrderVO order
    );

    @PutMapping("/v1/receiving/{pKey}")
    ReceivingOrderVO changeOrder(
            @PathVariable("pKey") String pKey,
            @RequestBody ReceivingOrderVO order
    );

    @PostMapping("/v1/receiving-orders/{pKey}/capture")
    void captureOrder(
            @PathVariable("pKey") String pKey,
            @RequestBody CaptureRequestVO request
    );

    @DeleteMapping("/v1/receiving-orders/{pKey}")
    void cancelOrder(
            @PathVariable("pKey") String pKey
    );
}
