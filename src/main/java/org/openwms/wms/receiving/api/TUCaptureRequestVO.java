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
package org.openwms.wms.receiving.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * A TUCaptureRequestVO contains all information used to capture goods within a {@code LoadUnit} on top of an expected {@code TransportUnit}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TUCaptureRequestVO extends CaptureRequestVO implements Serializable {

    /** The business key of the actual captured {@code TransportUnit} where the goods are placed on. */
    @NotBlank
    @JsonProperty("transportUnitBK")
    private String transportUnitId;

    /** The unique identifier if the {@code LoadUnit} where the goods are stored in. */
    @NotBlank
    @JsonProperty("loadUnitLabel")
    private String loadUnitLabel;

    /** The type in case a {@code LoadUnit} needs to be created (optional). */
    @JsonProperty("loadUnitType")
    private String loadUnitType;

    /** The business key of the expected {@code TransportUnit} to receive. */
    @NotBlank
    @JsonProperty("expectedTransportUnitBK")
    private String expectedTransportUnitBK;

    /** The ERP code of the actual {@code Location} where the goods were received. */
    @NotBlank
    @JsonProperty("actualLocationErpCode")
    private String actualLocationErpCode;

    public String getTransportUnitId() {
        return transportUnitId;
    }

    public void setTransportUnitId(String transportUnitId) {
        this.transportUnitId = transportUnitId;
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

    public String getActualLocationErpCode() {
        return actualLocationErpCode;
    }

    public void setActualLocationErpCode(String actualLocationErpCode) {
        this.actualLocationErpCode = actualLocationErpCode;
    }
}
