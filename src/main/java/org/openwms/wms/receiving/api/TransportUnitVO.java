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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;

import java.io.Serializable;
import java.util.Objects;

/**
 * A TransportUnitVO.
 *
 * @author Heiko Scherrer
 */
public class TransportUnitVO extends AbstractBase<LocationVO> implements Serializable {

    @JsonProperty("transportUnitBK")
    private String transportUnitId;

    /** The type of {@code TransportUnit} in case it needs to be created (optional). */
    @JsonProperty("transportUnitType")
    private String transportUnitType;

    public TransportUnitVO() {
    }

    public TransportUnitVO(String transportUnitId) {
        this.transportUnitId = transportUnitId;
    }

    public String getTransportUnitId() {
        return transportUnitId;
    }

    public void setTransportUnitId(String transportUnitId) {
        this.transportUnitId = transportUnitId;
    }

    public String getTransportUnitType() {
        return transportUnitType;
    }

    public void setTransportUnitType(String transportUnitType) {
        this.transportUnitType = transportUnitType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportUnitVO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(transportUnitId, that.transportUnitId) && Objects.equals(transportUnitType, that.transportUnitType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transportUnitId, transportUnitType);
    }
}
