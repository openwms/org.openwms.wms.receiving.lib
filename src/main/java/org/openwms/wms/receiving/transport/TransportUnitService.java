/*
 * Copyright 2005-2022 the original author or authors.
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

import javax.validation.constraints.NotNull;

/**
 * A TransportUnitService.
 *
 * @author Heiko Scherrer
 */
public interface TransportUnitService {

    /**
     * Create or update a {@link TransportUnit} instance.
     *
     * @param transportUnit Instance to save
     * @return Saved instance
     */
    TransportUnit upsert(@NotNull TransportUnit transportUnit);
}
