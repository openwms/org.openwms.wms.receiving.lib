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
package org.openwms.wms.transport.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.core.SpringProfiles;
import org.openwms.wms.transport.impl.RepositoryAccessor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A TransportUnitEventListenerTest.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Transactional
@SpringBootTest
class TransportUnitEventListenerTest {

    @Autowired
    private AmqpTemplate template;
    @Autowired
    private RepositoryAccessor accessor;
    @Value("${owms.receiving.orders.exchange-name}")
    private String exchangeName;
    @Value("${owms.receiving.orders.routing-key}")
    private String routingKey;

    @Commit
    public void delete() {
        accessor.getRepository().deleteAll();
    }

    @BeforeEach
    void onSetup() {
        this.delete();
    }

    @Test
    void shall_create_TU() throws Exception {
        int exists = accessor.getRepository().findAll().size();
        System.out.println(exists);
        assertThat(exists).isEqualTo(0);
        assertThat(exists).isZero();

        TransportUnitMO create = TransportUnitMO.newBuilder().withBarcode("4711").build();
        template.convertAndSend(exchangeName, "tu.event.created", create);
        TimeUnit.MILLISECONDS.sleep(500);
        assertThat(accessor.getRepository().findAll().size()).isEqualTo(exists + 1);

        create.setActualLocation("INIT/0000/0000/0000/0000");
        template.convertAndSend(exchangeName, "tu.event.changed", create);
        TimeUnit.MILLISECONDS.sleep(500);
        assertThat(accessor.getRepository().findAll().get(1).getActualLocation()).isEqualTo("INIT/0000/0000/0000/0000");
    }
}