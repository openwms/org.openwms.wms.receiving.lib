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
package org.openwms.wms.receiving.transport.event;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openwms.common.location.api.messages.LocationMO;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.core.SpringProfiles;
import org.openwms.wms.receiving.ReceivingApplicationTest;
import org.openwms.wms.receiving.transport.impl.RepositoryAccessor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A TransportUnitEventListenerIT.
 *
 * @author Heiko Scherrer
 */
@Disabled("Fails on CI")
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@ReceivingApplicationTest
class TransportUnitEventListenerIT {

    @Autowired
    private AmqpTemplate template;
    @Autowired
    private RepositoryAccessor accessor;
    @Value("${owms.events.common.tu.exchange-name}")
    private String exchangeName;
    @Autowired
    TransactionTemplate txTemplate;

    @Test
    void shall_create_TU() throws Exception {
        assertThat(accessor.getRepository().findAll().size()).isZero();

        TransportUnitMO create = TransportUnitMO.newBuilder().withBarcode("4711").withActualLocation(LocationMO.ofId("EXT_/0000/0000/0000/0000")).build();
        template.convertAndSend(exchangeName, "tu.event.created", create);
        TimeUnit.MILLISECONDS.sleep(500);
        assertThat(accessor.getRepository().findAll().size()).isEqualTo(1);

        create.setActualLocation(LocationMO.ofId("INIT/0000/0000/0000/0000"));
        template.convertAndSend(exchangeName, "tu.event.moved.INIT/0000/0000/0000/0000", create);
        TimeUnit.MILLISECONDS.sleep(500);
        assertThat(accessor.getRepository().findAll()).hasSize(1);
        assertThat(accessor.getRepository().findAll().get(0).getActualLocation()).isEqualTo("INIT/0000/0000/0000/0000");
    }
}