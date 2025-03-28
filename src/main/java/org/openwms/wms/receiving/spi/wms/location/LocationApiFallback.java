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
package org.openwms.wms.receiving.spi.wms.location;

import org.ameba.annotation.Measured;
import org.openwms.wms.receiving.spi.common.location.CommonLocationApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * A LocationApiFallback.
 *
 * @author Heiko Scherrer
 */
class LocationApiFallback implements LocationApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocationApiFallback.class);
    private final LocationMapper mapper;
    private final CommonLocationApi commonLocationApi;

    LocationApiFallback(LocationMapper mapper, CommonLocationApi commonLocationApi) {
        this.mapper = mapper;
        this.commonLocationApi = commonLocationApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public Optional<LocationVO> findByErpCodeOpt(String erpCode) {
        LOGGER.warn("WMS LocationApi not available or took too long, calling the COMMON LocationApi instead");
        return commonLocationApi.findByErpCode(erpCode).map(mapper::convertBetween);
    }
}
