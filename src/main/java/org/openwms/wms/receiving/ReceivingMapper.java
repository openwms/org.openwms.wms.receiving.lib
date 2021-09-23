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
package org.openwms.wms.receiving;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openwms.wms.receiving.api.BaseReceivingOrderPositionVO;
import org.openwms.wms.receiving.api.ProductVO;
import org.openwms.wms.receiving.api.ReceivingOrderPositionVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.api.ReceivingTransportUnitOrderPositionVO;
import org.openwms.wms.receiving.impl.BaseReceivingOrderPosition;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingOrderPosition;
import org.openwms.wms.receiving.impl.ReceivingTransportUnitOrderPosition;
import org.openwms.wms.receiving.inventory.Product;

import java.util.List;

/**
 * A ReceivingMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReceivingMapper {

    default BaseReceivingOrderPosition toLib(BaseReceivingOrderPositionVO refinement, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        if (refinement instanceof ReceivingOrderPositionVO) {
            return convertToVO((ReceivingOrderPositionVO) refinement, cycleAvoidingMappingContext);
        } else if (refinement instanceof ReceivingTransportUnitOrderPositionVO) {
            return convertToVO((ReceivingTransportUnitOrderPositionVO) refinement, cycleAvoidingMappingContext);
        } else {
            return null;
        }
    }

    default BaseReceivingOrderPositionVO toVO(BaseReceivingOrderPosition refinement, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext) {
        if (refinement instanceof ReceivingOrderPosition) {
            return convertToVO((ReceivingOrderPosition) refinement, cycleAvoidingMappingContext);
        } else if (refinement instanceof ReceivingTransportUnitOrderPosition) {
            return convertToVO((ReceivingTransportUnitOrderPosition) refinement, cycleAvoidingMappingContext);
        } else {
            return null;
        }
    }

    ProductVO convertToVO(Product eo);

    @Mapping(target = "orderState", source = "state")
    @Mapping(target = "positions", source = "positions")
    ReceivingOrder convertVO(ReceivingOrderVO vo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "pKey", source = "persistentKey")
    @Mapping(target = "state", source = "orderState")
    ReceivingOrderVO convertToVO(ReceivingOrder eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "pKey", source = "persistentKey")
    @Mapping(target = "state", source = "orderState")
    List<ReceivingOrderVO> convertToVO(List<ReceivingOrder> eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "positionId", source = "posNo")
    @Mapping(target = "quantityExpected", source = "quantityExpected")
    ReceivingOrderPositionVO convertToVO(ReceivingOrderPosition eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "positionId", source = "posNo")
    @Mapping(target = "order", source = "order")
    ReceivingTransportUnitOrderPositionVO convertToVO(ReceivingTransportUnitOrderPosition eo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "posNo", source = "positionId")
    @Mapping(target = "order", source = "order")
    @Mapping(target = "details", source = "details")
    ReceivingTransportUnitOrderPosition convertToVO(ReceivingTransportUnitOrderPositionVO vo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mapping(target = "posNo", source = "positionId")
    @Mapping(target = "quantityExpected", source = "quantityExpected")
    @Mapping(target = "details", source = "details")
    ReceivingOrderPosition convertToVO(ReceivingOrderPositionVO vo, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);
}
