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
package org.openwms.wms.receiving;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openwms.wms.receiving.api.BaseReceivingOrderPositionVO;
import org.openwms.wms.receiving.api.ReceivingOrderPositionVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.api.ReceivingTransportUnitOrderPositionVO;
import org.openwms.wms.receiving.impl.AbstractReceivingOrderPosition;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingOrderPosition;
import org.openwms.wms.receiving.impl.ReceivingTransportUnitOrderPosition;

import java.util.List;

import static java.lang.String.format;

/**
 * A ReceivingMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReceivingMapper {

    default AbstractReceivingOrderPosition fromVOtoEO(BaseReceivingOrderPositionVO vo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        if (vo instanceof ReceivingOrderPositionVO rop) {
            return convertFromVO(rop, cycleAvoidingMappingContext);
        } else if (vo instanceof ReceivingTransportUnitOrderPositionVO rtuop) {
            return convertFromVO(rtuop, cycleAvoidingMappingContext);
        } else if (vo != null){
            throw new UnsupportedOperationException(format("ReceivingOrderPosition type [%s] is not supported", vo.getClass()));
        } else {
            throw new UnsupportedOperationException("ReceivingOrderPosition to convert is null");
        }
    }

    default BaseReceivingOrderPositionVO fromEOtoVO(AbstractReceivingOrderPosition eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        if (eo instanceof ReceivingOrderPosition rop) {
            return convertToVO(rop, cycleAvoidingMappingContext);
        } else if (eo instanceof ReceivingTransportUnitOrderPosition rtuop) {
            return convertToVO(rtuop, cycleAvoidingMappingContext);
        } else if (eo != null){
            throw new UnsupportedOperationException(format("ReceivingOrderPosition type [%s] is not supported", eo.getClass()));
        } else {
            throw new UnsupportedOperationException("ReceivingOrderPosition to convert is null");
        }
    }

    @Mapping(target = "orderState", source = "state")
    @Mapping(target = "positions", source = "positions")
    ReceivingOrder convertVO(ReceivingOrderVO vo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "pKey", source = "persistentKey")
    @Mapping(target = "state", source = "orderState")
    ReceivingOrderVO convertToVO(ReceivingOrder eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "pKey", source = "persistentKey")
    @Mapping(target = "state", source = "orderState")
    List<ReceivingOrderVO> convertToVO(List<ReceivingOrder> eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    /*~ Positions */
    @Mapping(target = "orderId", source = "eo.order.orderId")
    @Mapping(target = "priority", source = "eo.order.priority")
    @Mapping(target = "positionId", source = "posNo")
    @Mapping(target = "quantityExpected", source = "quantityExpected")
    @Mapping(target = "quantityReceived", source = "quantityReceived")
    @Mapping(target = "details", source = "details")
    @Mapping(target = "createDt", source = "createDt")
    ReceivingOrderPositionVO convertToVO(ReceivingOrderPosition eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "positionId", source = "posNo")
    @Mapping(target = "order", source = "order")
    ReceivingTransportUnitOrderPositionVO convertToVO(ReceivingTransportUnitOrderPosition eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "posNo", source = "positionId")
    @Mapping(target = "order", source = "order")
    @Mapping(target = "details", source = "details")
    ReceivingTransportUnitOrderPosition convertFromVO(ReceivingTransportUnitOrderPositionVO vo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "posNo", source = "positionId")
    @Mapping(target = "quantityExpected", source = "quantityExpected")
    @Mapping(target = "details", source = "details")
    ReceivingOrderPosition convertFromVO(ReceivingOrderPositionVO vo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
