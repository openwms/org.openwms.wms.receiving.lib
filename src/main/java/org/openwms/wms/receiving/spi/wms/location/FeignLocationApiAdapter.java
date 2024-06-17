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
import org.ameba.annotation.Measured;
import org.openwms.core.SpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

/**
 * A FeignLocationApiAdapter is only active with Spring profile {@literal DISTRIBUTED} and uses the Feign {@code Location API}.
 *
 * @author Heiko Scherrer
 * @see LocationApi
 */
@Profile(SpringProfiles.DISTRIBUTED)
@Validated
@Component
class FeignLocationApiAdapter implements SyncLocationApi {

    private final LocationApi locationApi;

    FeignLocationApiAdapter(LocationApi locationApi) {
        this.locationApi = locationApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<LocationVO> findByErpCodeOpt(@NotBlank String erpCode) {
        return locationApi.findByErpCodeOpt(erpCode);
    }
}
