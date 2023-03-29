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
package org.openwms.wms.receiving.spi.wms.transport;

import feign.FeignException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.exception.ServiceLayerException;
import org.ameba.i18n.Translator;
import org.openwms.core.SpringProfiles;
import org.openwms.wms.receiving.spi.common.transport.CommonTransportUnitApi;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A TransportUnitApiFallbackFactory.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.DISTRIBUTED)
@Component
class TransportUnitApiFallbackFactory implements FallbackFactory<TransportUnitApi> {

    private final CommonTransportUnitApi commonTransportUnitApi;
    private final Translator translator;

    TransportUnitApiFallbackFactory(CommonTransportUnitApi commonTransportUnitApi, Translator translator) {
        this.commonTransportUnitApi = commonTransportUnitApi;
        this.translator = translator;
    }

    @Override
    public TransportUnitApi create(Throwable cause) {
        cause.printStackTrace();
        if (cause instanceof FeignException fe) {
            if (fe.status() == 404) {
                // If TU not exists in Inventory Service...
                return new TransportUnitApiFallback(commonTransportUnitApi, translator);
            }
            if (fe.status() > 400) {
                if (fe.status() == 409) {
                    // If TU already exists in Inventory Service...break!
                    throw new ResourceExistsException(fe.getMessage());
                }
                throw new ServiceLayerException(fe.getMessage());
            }
        }
        // default hand over to fallback
        return new TransportUnitApiFallback(commonTransportUnitApi, translator);
    }
}
