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

import org.ameba.annotation.Measured;
import org.openwms.core.SpringProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * A NoOpSyncProductApiImpl.
 *
 * @author Heiko Scherrer
 */
@Profile("!" + SpringProfiles.DISTRIBUTED)
@Validated
@Component
class NoOpSyncProductApiImpl implements SyncProductApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpSyncProductApiImpl.class);

    /**
     * {@inheritDoc}
     *
     * No operation here!
     */
    @Override
    @Measured
    public ProductVO findBySKU(@NotBlank String sku) {
        LOGGER.error("Not implemented yet");
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * No operation here!
     */
    @Override
    @Measured
    public ProductVO findProductByProductUnitPkey(@NotBlank String pKey) {
        LOGGER.error("Not implemented yet");
        return null;
    }
}
