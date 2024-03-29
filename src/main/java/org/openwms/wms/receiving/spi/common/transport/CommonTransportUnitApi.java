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
package org.openwms.wms.receiving.spi.common.transport;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * A CommonTransportUnitApi.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", qualifiers = "commonTransportUnitApi")
public interface CommonTransportUnitApi {

    /**
     * Move a {@code TransportUnit} from its current location to the {@code newLocation}.
     *
     * @param transportUnitBK The unique (physical) identifier
     * @param newLocation The new {@code Location} to move to
     * @return The updated instance
     */
    @PatchMapping(value = "/v1/transport-units", params = {"bk", "newLocation"})
    Object moveTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam("newLocation") String newLocation
    );

    /**
     * Create a {@code TransportUnit} with the given (minimal) information.
     *
     * @param transportUnitBK The unique (physical) identifier
     * @param actualLocation The current location of the {@code TransportUnit}
     * @param tut The type ({@code TransportUnitType}
     * @param strict If the {@code TransportUnit} already exists and this is {@code true} an error is thrown. If
     * {@code false}, the implementation exists silently (default)
     */
    @PostMapping(value = "/v1/transport-units", params = {"bk", "actualLocation", "tut"})
    void createTU(
            @RequestParam("bk") String transportUnitBK,
            @RequestParam("actualLocation") String actualLocation,
            @RequestParam("tut") String tut,
            @RequestParam(value = "strict", required = false) Boolean strict
    );
}
