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

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

/**
 * A SyncLocationApi is the public API to manage {@code Locations} in a synchronous way.
 *
 * @author Heiko Scherrer
 */
public interface SyncLocationApi {

    /**
     * Find and return a {@code Location} representation by the given {@code erpCode}.
     *
     * @param erpCode The ERP code
     * @return The instance
     */
    Optional<LocationVO> findByErpCodeOpt(@NotBlank String erpCode);
}
