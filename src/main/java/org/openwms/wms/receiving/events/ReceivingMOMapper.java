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
package org.openwms.wms.receiving.events;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openwms.wms.receiving.CycleAvoidingMappingContext;
import org.openwms.wms.receiving.api.messages.ReceivingOrderMO;
import org.openwms.wms.receiving.api.messages.ReceivingOrderPositionMO;
import org.openwms.wms.receiving.impl.AbstractReceivingOrderPosition;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingOrderPosition;
import org.openwms.wms.receiving.impl.ReceivingTransportUnitOrderPosition;

import static java.lang.String.format;

/**
 * A ReceivingMOMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReceivingMOMapper {

    @Mapping(target = "pKey", source = "persistentKey")
    @Mapping(target = "state", source = "orderState")
    ReceivingOrderMO convertToMO(ReceivingOrder eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "positionId", source = "posNo")
    ReceivingOrderPositionMO convertToReceivingOrderPositionMO(ReceivingOrderPosition eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "positionId", source = "posNo")
    ReceivingOrderPositionMO convertToReceivingOrderPositionMO(ReceivingTransportUnitOrderPosition eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    default ReceivingOrderPositionMO fromEOtoMO(AbstractReceivingOrderPosition eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        if (eo instanceof ReceivingOrderPosition rop) {
            return convertToReceivingOrderPositionMO(rop, cycleAvoidingMappingContext);
        } else if (eo instanceof ReceivingTransportUnitOrderPosition rtuop) {
            return convertToReceivingOrderPositionMO(rtuop, cycleAvoidingMappingContext);
        } else if (eo != null){
            throw new UnsupportedOperationException(format("ReceivingOrderPosition type [%s] is not supported", eo.getClass()));
        } else {
            throw new UnsupportedOperationException("ReceivingOrderPosition to convert is null");
        }
    }
}
