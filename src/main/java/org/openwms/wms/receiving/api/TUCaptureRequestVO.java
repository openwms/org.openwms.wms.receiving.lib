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
package org.openwms.wms.receiving.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * A CaptureRequestVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TUCaptureRequestVO extends CaptureRequestVO implements Serializable {

    /** The business key of the captured TransportUnit. */
    @NotEmpty
    @JsonProperty("transportUnitBK")
    private String transportUnitId;
    /** The unique */
    @NotEmpty
    @JsonProperty("loadUnitLabel")
    private String loadUnitLabel;
    /** The business key of the captured TransportUnit. */
    @NotEmpty
    @JsonProperty("expectedTransportUnitBK")
    private String expectedTransportUnitBK;
    @NotEmpty
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
