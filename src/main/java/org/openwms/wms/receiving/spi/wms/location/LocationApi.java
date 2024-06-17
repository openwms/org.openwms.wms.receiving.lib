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
package org.openwms.wms.receiving.spi.wms.location;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * A LocationApi deals with {@code Location}s.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "wms-inventory", qualifiers = "wmsLocationApi", dismiss404 = true, fallbackFactory = LocationApiFallbackFactory.class)
interface LocationApi {

    /**
     * Find and return a {@code Location} representation by the given {@code erpCode}.
     *
     * @param erpCode The ERP code
     * @return Never {@literal null}
     */
    @GetMapping(value = "/v1/locations", params = {"erpCode"})
    @Cacheable("wmsLocations")
    Optional<LocationVO> findByErpCodeOpt(
            @RequestParam("erpCode") String erpCode
    );
}
