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
package org.openwms.wms.receiving.spi.wms.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * A PackagingUnitApi is the Feign client used internally, not by any business logic directly.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "wms-inventory", dismiss404 = true, qualifiers = "packagingUnitApi", fallback = PackagingUnitApiFallback.class)
interface PackagingUnitApi {

    /**
     * Create a new {@code PackagingUnit} on the {@code Location} given as {@code actualLocation} of the {@code pu}.
     *
     * @param pu The PackagingUnit representation, contains the Location where to create it
     */
    @PostMapping("/v1/packaging-units")
    void createOnLocation(@RequestBody List<PackagingUnitVO> pus);
}
