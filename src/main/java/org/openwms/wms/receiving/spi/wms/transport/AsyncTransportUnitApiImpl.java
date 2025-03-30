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
package org.openwms.wms.receiving.spi.wms.transport;

import org.ameba.annotation.Measured;
import org.openwms.common.transport.api.commands.Command;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.core.SpringProfiles;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * A AsyncTransportUnitApiImpl is a Spring managed bean to send Commands asynchronously over AMQP, only active with Spring profile
 * {@linkplain SpringProfiles#ASYNCHRONOUS_PROFILE}.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.ASYNCHRONOUS_PROFILE)
@Component
class AsyncTransportUnitApiImpl implements AsyncTransportUnitApi {

    private final AmqpTemplate template;
    private final String exchangeName;

    AsyncTransportUnitApiImpl(
            AmqpTemplate template,
            @Value("${owms.commands.common.tu.exchange-name}") String exchangeName
    ) {
        this.template = template;
        this.exchangeName = exchangeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void process(Command<?> command) {
        if (command instanceof TUCommand tuCommand) {
            if (Objects.requireNonNull(tuCommand.getType()) == TUCommand.Type.CREATE) {
                template.convertAndSend(exchangeName, "common.tu.command.in.create", tuCommand);
            } else if (tuCommand.getType() == TUCommand.Type.CHANGE_ACTUAL_LOCATION) {
                template.convertAndSend(exchangeName, "common.tu.command.in.move", tuCommand);
            }
        }
    }
}
