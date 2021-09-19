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

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openwms.wms.receiving.api.ProductVO;
import org.openwms.wms.receiving.api.ReceivingOrderPositionVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.BaseReceivingOrderPositionVisitor;
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
public abstract class ReceivingMapper implements BaseReceivingOrderPositionVisitor<ReceivingOrderPositionVO> {
    @Override
    public ReceivingOrderPositionVO visit(ReceivingOrderPosition position) {
        return convertToVO(position);
    }

    @Override
    public ReceivingOrderPositionVO visit(ReceivingTransportUnitOrderPosition position) {
        return convertToVO(position);
    }

    abstract ProductVO convertToVO(Product eo);

    @Mapping(target = "pKey", source = "persistentKey")
    @Mapping(target = "state", source = "orderState")
    abstract ReceivingOrderVO convertToVO(ReceivingOrder eo);

    @Mapping(target = "pKey", source = "persistentKey")
    @Mapping(target = "state", source = "orderState")
    abstract List<ReceivingOrderVO> convertToVO(List<ReceivingOrder> eo);

    @Mapping(target = "positionId", source = "posNo")
    @Mapping(target = "quantityExpected", source = "quantityExpected")
    abstract ReceivingOrderPositionVO convertToVO(ReceivingOrderPosition eo);

    @Mapping(target = "positionId", source = "posNo")
    abstract ReceivingOrderPositionVO convertToVO(ReceivingTransportUnitOrderPosition eo);
}
