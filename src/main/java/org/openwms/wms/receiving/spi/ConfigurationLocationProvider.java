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
package org.openwms.wms.receiving.spi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A ConfigurationLocationProvider.
 *
 * @author Heiko Scherrer
 */
@Component
class ConfigurationLocationProvider implements InitialLocationProvider {

    private final String initialLocationErpCode;

    ConfigurationLocationProvider(@Value("${owms.receiving.initial-location-id}") String initialLocationErpCode) {
        this.initialLocationErpCode = initialLocationErpCode;
    }

    @Override
    public String findInitial() {
        return initialLocationErpCode;
    }
}
