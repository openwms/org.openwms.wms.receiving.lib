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
package org.openwms.wms.receiving.spi.wms.transport;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A TransportUnitApi is the public REST API to manage {@code TransportUnits}. It is implemented by a {@code Feign} client stub.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "wms-inventory", qualifiers = "wmsTransportUnitApi", dismiss404 = true, fallbackFactory = TransportUnitApiFallbackFactory.class)
interface TransportUnitApi {

    /**
     * Move a {@code TransportUnit} from its current location to the {@code newLocation}.
     *
     * @param transportUnitBK The unique (physical) identifier
     * @param newLocationErpCode The ERPCode of the {@code Location} to move to
     */
    @PatchMapping(value = "/v1/transport-units", params = {"bk", "newLocation"})
    void moveTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam("newLocation") String newLocationErpCode
    );

    /**
     * Create a {@code TransportUnit}.
     *
     * @param tu Detailed information of the {@code TransportUnit} to create
     */
    @PostMapping("/v1/transport-units")
    void createTU(
            @RequestBody TransportUnitVO tu
    );
}
