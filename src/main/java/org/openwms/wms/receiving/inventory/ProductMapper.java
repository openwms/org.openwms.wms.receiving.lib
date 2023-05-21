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
package org.openwms.wms.receiving.inventory;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openwms.wms.receiving.inventory.events.InventoryProductMO;
import org.openwms.wms.receiving.spi.wms.inventory.ProductVO;

/**
 * A ProductMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "foreignPKey", source = "pKey")
    @Mapping(target = "overbookingAllowed", source = "overbookingAllowed")
    @Mapping(target = "ol", ignore = true)
    Product convertFromMO(InventoryProductMO mo);

    @Mapping(target = "foreignPKey", source = "pKey")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "label", source = "label")
    @Mapping(target = "baseUnit", source = "baseUnit")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "overbookingAllowed", source = "overbookingAllowed")
    @Mapping(target = "ol", ignore = true)
    Product convertFromVO(ProductVO vo);
}
