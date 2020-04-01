/*
 * Copyright 2005-2020 the original author or authors.
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
package org.openwms.wms.transport;

import org.ameba.exception.ServiceLayerException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A TransportUnitServiceImplTest.
 *
 * @author Heiko Scherrer
 */
@SpringBootTest
class TransportUnitServiceImplTest {

    @Autowired
    private TransportUnitService service;

    @Test
    void upsertWithNull() {
        Exception ex = assertThrows(ServiceLayerException.class, () -> service.upsert(null));
        assertThat(ex.getMessage()).containsIgnoringCase("transportUnit");
        ex = assertThrows(ServiceLayerException.class, () -> service.upsert(new TransportUnit()));
        assertThat(ex.getMessage()).containsIgnoringCase("barcode");
    }

    @Test
    void upsert() {
        TransportUnit tu = new TransportUnit("4709", "EXT_/0000/0000/0000/0000");
        tu = service.upsert(tu);
        assertThat(tu.getBarcode()).isEqualTo("4709");
        assertThat(tu.isNew()).isFalse();

        tu = service.upsert(tu);
        assertThat(tu.getBarcode()).isEqualTo("4709");
        assertThat(tu.isNew()).isFalse();

        tu = service.upsert(new TransportUnit("4709", "EXT_/0000/0000/0000/0000"));
        assertThat(tu.getBarcode()).isEqualTo("4709");
        assertThat(tu.isNew()).isFalse();
    }
}