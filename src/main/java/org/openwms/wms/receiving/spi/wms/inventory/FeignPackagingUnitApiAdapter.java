/*
 * Copyright 2005-2025 the original author or authors.
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

import org.ameba.annotation.Measured;
import org.openwms.core.SpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A FeignPackagingUnitApiAdapter.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.DISTRIBUTED)
@Component
class FeignPackagingUnitApiAdapter implements SyncPackagingUnitApi {

    private final PackagingUnitApi packagingUnitApi;

    FeignPackagingUnitApiAdapter(PackagingUnitApi packagingUnitApi) {
        this.packagingUnitApi = packagingUnitApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void createOnLocation(List<PackagingUnitVO> pus) {
        packagingUnitApi.createOnLocation(pus);
    }
}
