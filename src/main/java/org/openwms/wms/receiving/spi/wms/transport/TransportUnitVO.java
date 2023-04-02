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
package org.openwms.wms.receiving.spi.wms.transport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;
import org.openwms.wms.receiving.spi.wms.location.LocationVO;

import java.io.Serializable;
import java.util.Objects;

/**
 * A TransportUnitVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TransportUnitVO extends AbstractBase<TransportUnitVO> implements Serializable {

    public static final String MEDIA_TYPE = "application/vnd.openwms.wms.transport-unit-v1+json";

    @JsonProperty("transportUnitBK")
    private String transportUnitBK;

    /** The actual {@code Location}. */
    @JsonProperty("actualLocation")
    private LocationVO actualLocation;

    /** The name of the TransportUnit's type. */
    @JsonProperty("transportUnitType")
    private String transportUnitType;

    /*~-------------------- constructors --------------------*/
    @JsonCreator
    protected TransportUnitVO() {}

    public TransportUnitVO(String transportUnitBK, LocationVO actualLocation, String transportUnitType) {
        Objects.requireNonNull(transportUnitBK);
        Objects.requireNonNull(actualLocation);
        Objects.requireNonNull(transportUnitType);
        this.transportUnitBK = transportUnitBK;
        this.actualLocation = actualLocation;
        this.transportUnitType = transportUnitType;
    }

    /*~-------------------- methods --------------------*/
    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportUnitVO that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(transportUnitBK, that.transportUnitBK) && Objects.equals(actualLocation, that.actualLocation) && Objects.equals(transportUnitType, that.transportUnitType);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transportUnitBK, actualLocation, transportUnitType);
    }

    /*~-------------------- accessors --------------------*/
    public String getTransportUnitBK() {
        return transportUnitBK;
    }

    public LocationVO getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(LocationVO actualLocation) {
        this.actualLocation = actualLocation;
    }

    public String getTransportUnitType() {
        return transportUnitType;
    }

    public void setTransportUnitType(String transportUnitType) {
        this.transportUnitType = transportUnitType;
    }
}
