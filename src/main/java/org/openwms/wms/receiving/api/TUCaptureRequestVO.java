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
package org.openwms.wms.receiving.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.wms.receiving.ValidationGroups;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A TUCaptureRequestVO contains all information used to capture goods within a {@code LoadUnit} on top of an expected {@code TransportUnit}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TUCaptureRequestVO extends CaptureRequestVO implements Serializable {

    /** The actual captured {@code TransportUnit} where the goods are located on. */
    @JsonProperty("transportUnit")
    @NotNull(groups = {ValidationGroups.CreateBlindTUReceipt.class, ValidationGroups.CreateExpectedTUReceipt.class})
    private TransportUnitVO transportUnit;

    /** The unique identifier if the {@code LoadUnit} where the goods are stored in. */
    @JsonProperty("loadUnitLabel")
    @NotBlank(groups = ValidationGroups.CreateExpectedTUReceipt.class)
    private String loadUnitLabel;

    /** The type in case a {@code LoadUnit} needs to be created (optional). */
    @JsonProperty("loadUnitType")
    private String loadUnitType;

    /** The business key of the expected {@code TransportUnit} to receive. */
    @JsonProperty("expectedTransportUnitBK")
    @NotBlank(groups = ValidationGroups.CreateExpectedTUReceipt.class)
    private String expectedTransportUnitBK;

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

    public String getLoadUnitLabel() {
        return loadUnitLabel;
    }

    public void setLoadUnitLabel(String loadUnitLabel) {
        this.loadUnitLabel = loadUnitLabel;
    }

    public String getLoadUnitType() {
        return loadUnitType;
    }

    public void setLoadUnitType(String loadUnitType) {
        this.loadUnitType = loadUnitType;
    }

    public String getExpectedTransportUnitBK() {
        return expectedTransportUnitBK;
    }

    public void setExpectedTransportUnitBK(String expectedTransportUnitBK) {
        this.expectedTransportUnitBK = expectedTransportUnitBK;
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
