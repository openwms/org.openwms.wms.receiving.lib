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
package org.openwms.wms.receiving.spi;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.openwms.common.location.api.messages.LocationMO;
import org.openwms.common.transport.api.commands.TUCommand;
import org.openwms.common.transport.api.messages.TransportUnitMO;
import org.openwms.common.transport.api.messages.TransportUnitTypeMO;
import org.openwms.wms.receiving.ProcessingException;
import org.openwms.wms.receiving.impl.AbstractReceivingOrderPosition;
import org.openwms.wms.receiving.impl.OrderPositionProcessor;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingTransportUnitOrderPosition;
import org.openwms.wms.receiving.spi.wms.transport.AsyncTransportUnitApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * A DefaultOrderPositionProcessor creates expected {@code TransportUnit}s when the {@code transportUnitBK} and the
 * {@code transportUnitTypeName} are given.
 *
 * @author Heiko Scherrer
 */
@ConditionalOnProperty(name = "owms.receiving.create-tu-on-expected-tu-receipt", havingValue = "true")
@TxService
class DefaultOrderPositionProcessor implements OrderPositionProcessor {

    private final AsyncTransportUnitApi asyncTransportUnitApi;
    private final InitialLocationProvider initialLocationProvider;

    DefaultOrderPositionProcessor(AsyncTransportUnitApi asyncTransportUnitApi, InitialLocationProvider initialLocationProvider) {
        this.asyncTransportUnitApi = asyncTransportUnitApi;
        this.initialLocationProvider = initialLocationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = {IllegalArgumentException.class, ProcessingException.class})
    public void processPosition(ReceivingOrder order, AbstractReceivingOrderPosition orderPosition) {
        if (orderPosition instanceof ReceivingTransportUnitOrderPosition rtuop) {
            var type = TransportUnitTypeMO.newBuilder().type(rtuop.getTransportUnitTypeName());
            var initialLocation = initialLocationProvider.findInitial();
            asyncTransportUnitApi.process(
                    TUCommand.newBuilder(TUCommand.Type.CREATE)
                            .withTransportUnit(TransportUnitMO.newBuilder()
                                    .withBarcode(rtuop.getTransportUnitBK())
                                    .withActualLocation(LocationMO.ofId(initialLocation))
                                    .withTransportUnitType(type.build())
                                    .build()
                            )
                            .build()
            );
        }
    }
}
