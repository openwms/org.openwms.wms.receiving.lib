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
package org.openwms.wms.receiving.transport;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openwms.common.location.api.messages.LocationMO;
import org.openwms.common.transport.api.messages.TransportUnitMO;

/**
 * A TransportUnitMapper.
 *
 * @author Heiko Scherrer
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransportUnitMapper {

    @Mapping(target = "foreignPKey", source = "pKey")
    @Mapping(target = "ol", ignore = true)
    TransportUnit convertFromMO(TransportUnitMO mo);

    default String convertFromMO(LocationMO mo) {
        if (mo == null) {
            return null;
        }
        return mo.id();
    }
}
