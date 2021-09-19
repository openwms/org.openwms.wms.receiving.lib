/*
 * Copyright 2005-2021 the original author or authors.
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
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.common.transport.api.messages.TransportUnitTypeMO;
import org.openwms.wms.receiving.ProcessingException;
import org.openwms.wms.receiving.impl.BaseReceivingOrderPosition;
import org.openwms.wms.receiving.impl.OrderPositionProcessor;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingTransportUnitOrderPosition;
import org.openwms.wms.receiving.transport.api.AsyncTransportUnitApi;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * A DefaultOrderPositionProcessor creates expected {@code TransportUnit}s when the {@code transportUnitBK} and the
 * {@code transportUnitTypeName} are given.
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
    public void processPosition(ReceivingOrder order, BaseReceivingOrderPosition orderPosition) {
        if (orderPosition instanceof ReceivingTransportUnitOrderPosition) {
            TransportUnitTypeMO.Builder type = TransportUnitTypeMO.newBuilder()
                    .type(((ReceivingTransportUnitOrderPosition) orderPosition).getTransportUnitTypeName());
            // FIXME [openwms]: 15.08.21 Get the actualLocation from the current workplace or from YML config
            transportUnitApi.process(
                    TUCommand.newBuilder(TUCommand.Type.CREATE)
                            .withTransportUnit(TransportUnitMO.newBuilder()
                                    .withBarcode(((ReceivingTransportUnitOrderPosition) orderPosition).getTransportUnitBK())
                                    .withTransportUnitType(type.build())
                                    .build()
                            )
                            .build()
            );
        }
    }
}
