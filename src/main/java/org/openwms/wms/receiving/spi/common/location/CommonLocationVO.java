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
package org.openwms.wms.receiving.spi.common.location;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A CommonLocationVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class CommonLocationVO extends AbstractBase<CommonLocationVO> implements Serializable {

    /** Unique natural key. */
    @JsonProperty("locationId")
    private String locationId;
    /** ERP code of the {@code Location}. */
    @JsonProperty("erpCode")
    private String erpCode;

    /*~-------------------- constructors --------------------*/
    public CommonLocationVO() {}

    private CommonLocationVO(String locationId) {
        Objects.requireNonNull(locationId);
        this.locationId = locationId;
    }

    public static CommonLocationVO of(String locationId) {
        return new CommonLocationVO(locationId);
    }

    /*~-------------------- methods --------------------*/
    public boolean hasErpCode() {
        return erpCode != null && !erpCode.isEmpty();
    }

    public boolean hasLocationId() {
        return locationId != null && !locationId.isEmpty();
    }

    /*~-------------------- accessors --------------------*/
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    /*~-------------------- overrides --------------------*/
    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", CommonLocationVO.class.getSimpleName() + "[", "]")
                .add("locationId='" + locationId + "'")
                .add("erpCode='" + erpCode + "'")
                .toString();
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommonLocationVO)) return false;
        if (!super.equals(o)) return false;
        CommonLocationVO that = (CommonLocationVO) o;
        return Objects.equals(locationId, that.locationId) && Objects.equals(erpCode, that.erpCode);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), locationId, erpCode);
    }
}
