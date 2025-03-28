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
package org.openwms.wms.receiving.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.openwms.wms.receiving.ValidationGroups;

import java.io.Serializable;

/**
 * A TUCaptureRequestVO contains all information used to capture goods within a {@code LoadUnit} on top of an expected {@code TransportUnit}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TUCaptureRequestVO extends CaptureRequestVO implements Serializable {

    /** The actual captured {@code TransportUnit}. */
    @JsonProperty("transportUnit")
    @NotNull(groups = {ValidationGroups.CreateBlindTUReceipt.class, ValidationGroups.CreateExpectedTUReceipt.class})
    private TransportUnitVO transportUnit;

    /** The {@code Location} where the captured {@code TransportUnit} is located on. */
    @JsonProperty("actualLocation")
    @NotNull(groups = {ValidationGroups.CreateBlindTUReceipt.class, ValidationGroups.CreateExpectedTUReceipt.class})
    private LocationVO actualLocation;

    public TransportUnitVO getTransportUnit() {
        return transportUnit;
    }

    public void setTransportUnit(TransportUnitVO transportUnit) {
        this.transportUnit = transportUnit;
    }

    public boolean hasTransportUnitType() {
        return transportUnit != null && transportUnit.getTransportUnitType() != null && !transportUnit.getTransportUnitType().isEmpty();
    }

    public LocationVO getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(LocationVO actualLocation) {
        this.actualLocation = actualLocation;
    }
}
