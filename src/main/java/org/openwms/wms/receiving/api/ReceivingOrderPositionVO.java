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
package org.openwms.wms.receiving.api;

import org.openwms.core.units.api.Measurable;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * A ReceivingOrderPositionVO.
 *
 * @author Heiko Scherrer
 */
public class ReceivingOrderPositionVO implements Serializable {

    @NotEmpty
    private String positionId;
    @NotEmpty
    private Measurable<?, ?, ?> quantityExpected;
    private String sku;
    private String startMode;
    private String transportUnitId;
    private String transportUnitType;
    private String supplierPackingUnit;
}
