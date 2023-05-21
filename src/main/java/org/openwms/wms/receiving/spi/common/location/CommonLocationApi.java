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
package org.openwms.wms.receiving.spi.common.location;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * A CommonLocationApi defines the public REST API to manage {@code Location}s. It is a Feign remote stub that can be used by client
 * application.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "common-service", decode404 = true, qualifiers = "locationApi")
public interface CommonLocationApi {


    /**
     * Find and return a {@code Location} representation by the given {@code erpCode}.
     *
     * @param erpCode The ERP code
     * @return Never {@literal null}
     */
    @GetMapping(value = "/v1/locations", params = {"erpCode"}, produces = "application/vnd.openwms.common.location-opt-v1+json")
    @Cacheable("locations")
    Optional<CommonLocationVO> findByErpCode(@RequestParam("erpCode") String erpCode);
}
