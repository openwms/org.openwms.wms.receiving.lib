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
package org.openwms.wms.receiving.spi;

import org.ameba.annotation.TxService;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.common.transport.api.messages.TransportUnitTypeMO;
import org.openwms.wms.receiving.transport.api.AsyncTransportUnitApi;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.wms.receiving.ProcessingException;
import org.openwms.wms.receiving.impl.OrderPositionProcessor;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingOrderPosition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * A DefaultOrderPositionProcessor.
 *
 * @author Heiko Scherrer
 */
@TxService
class DefaultOrderPositionProcessor implements OrderPositionProcessor {

    private final AsyncTransportUnitApi transportUnitApi;

    DefaultOrderPositionProcessor(AsyncTransportUnitApi transportUnitApi) {
        this.transportUnitApi = transportUnitApi;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {IllegalArgumentException.class, ProcessingException.class})
    public void processPosition(ReceivingOrder order, ReceivingOrderPosition orderPosition) {
        TransportUnitTypeMO.Builder type = TransportUnitTypeMO.newBuilder()
                .type(orderPosition.getPositionDetails().getTransportUnitType());
        orderPosition.getPositionDetails().setHeightIfExists(type::height);
        orderPosition.getPositionDetails().setWidthIfExists(type::width);
        orderPosition.getPositionDetails().setLengthIfExists(type::length);
        transportUnitApi.process(
                TUCommand.newBuilder(TUCommand.Type.CREATE)
                        .withTransportUnit(TransportUnitMO.newBuilder()
                                .withBarcode(orderPosition.getTransportUnitBK())
                                .withTransportUnitType(type.build())
                                .build()
                        )
                        .build()
        );
    }
}
