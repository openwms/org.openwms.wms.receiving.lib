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
package org.openwms.wms.receiving.spi.wms.transport;

import org.openwms.core.SpringProfiles;
import org.openwms.wms.receiving.spi.common.transport.CommonTransportUnitApi;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A TransportUnitApiFallback.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.DISTRIBUTED)
@Component
class TransportUnitApiFallback implements TransportUnitApi {

    private final CommonTransportUnitApi commonTransportUnitApi;

    TransportUnitApiFallback(CommonTransportUnitApi commonTransportUnitApi) {
        this.commonTransportUnitApi = commonTransportUnitApi;
    }

    @Override
    public void moveTU(String transportUnitBK, String newLocationErpCode) {
        commonTransportUnitApi.moveTU(transportUnitBK, newLocationErpCode);
    }
}
