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
package org.openwms.wms.receiving.app;

import org.openwms.core.SpringProfiles;
import org.openwms.wms.receiving.spi.common.location.CommonLocationSpiPackage;
import org.openwms.wms.receiving.spi.common.transport.CommonTransportSpiPackage;
import org.openwms.wms.receiving.spi.wms.inventory.InventorySpiPackage;
import org.openwms.wms.receiving.spi.wms.location.LocationSpiPackage;
import org.openwms.wms.receiving.spi.wms.transport.TransportSpiPackage;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * A ReceivingFeignConfiguration.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.DISTRIBUTED)
@Configuration
@EnableFeignClients(basePackageClasses = {
        CommonTransportSpiPackage.class,
        CommonLocationSpiPackage.class,
        InventorySpiPackage.class,
        TransportSpiPackage.class,
        LocationSpiPackage.class
})
class ReceivingFeignConfiguration {
}
