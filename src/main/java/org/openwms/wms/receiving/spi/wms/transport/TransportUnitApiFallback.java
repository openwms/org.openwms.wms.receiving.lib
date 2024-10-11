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

import org.ameba.annotation.Measured;
import org.ameba.i18n.Translator;
import org.openwms.wms.receiving.spi.common.transport.CommonTransportUnitApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import static org.openwms.wms.receiving.ReceivingMessages.LOCATION_ID_NOT_GIVEN;

/**
 * A TransportUnitApiFallback.
 *
 * @author Heiko Scherrer
 */
class TransportUnitApiFallback implements TransportUnitApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportUnitApiFallback.class);
    private final Boolean strictlyCreateTU;
    private final CommonTransportUnitApi commonTransportUnitApi;
    private final Translator translator;

    TransportUnitApiFallback(@Value("${owms.receiving.create-tu-strictly}") Boolean strictlyCreateTU, CommonTransportUnitApi commonTransportUnitApi, Translator translator) {
        this.strictlyCreateTU = strictlyCreateTU;
        this.commonTransportUnitApi = commonTransportUnitApi;
        this.translator = translator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void moveTU(String transportUnitBK, String newLocationErpCode) {
        LOGGER.warn("TransportUnitApi not available or took too long, placing a command to move TransportUnit [{}] to [{}] afterwards",
                transportUnitBK, newLocationErpCode);
        commonTransportUnitApi.moveTU(transportUnitBK, newLocationErpCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void createTU(TransportUnitVO tu) {
        LOGGER.warn("TransportUnitApi not available or took too long, placing a command to create TransportUnit [{}] on [{}]",
                tu.getTransportUnitBK(), tu.getActualLocation().getLocationId());
        if (!tu.getActualLocation().hasLocationId()) {
            throw new IllegalArgumentException(translator.translate(LOCATION_ID_NOT_GIVEN));
        }
        commonTransportUnitApi.createTU(tu.getTransportUnitBK(), tu.getActualLocation().getLocationId(), tu.getTransportUnitType(), strictlyCreateTU);
    }
}
