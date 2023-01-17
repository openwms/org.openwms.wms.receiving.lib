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
package org.openwms.wms.receiving.transport.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.wms.receiving.transport.TransportUnit;
import org.openwms.wms.receiving.transport.TransportUnitService;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * A TransportUnitServiceImpl.
 *
 * @author Heiko Scherrer
 */
@Validated
@TxService
class TransportUnitServiceImpl implements TransportUnitService {

    private final TransportUnitRepository repository;

    TransportUnitServiceImpl(TransportUnitRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public TransportUnit upsert(@NotNull TransportUnit transportUnit) {
        Assert.notNull(transportUnit, "transportUnit must not be null");
        Assert.hasText(transportUnit.getBarcode(), "barcode of TransportUnit must be set before saving");
        TransportUnit merged = repository.findByBarcode(transportUnit.getBarcode()).orElse(transportUnit);
        merged.setActualLocation(transportUnit.getActualLocation());
        return repository.save(merged);
    }
}
