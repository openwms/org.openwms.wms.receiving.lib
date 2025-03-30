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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.openwms.wms.receiving.ValidationGroups;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ReceivingTransportUnitOrderPositionVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReceivingTransportUnitOrderPositionVO extends BaseReceivingOrderPositionVO implements ConvertableVO, Serializable {

    /** Expected receipts may also carry the unique identifier of the suppliers {@code TransportUnit}. */
    @JsonProperty("transportUnitBK")
    @NotBlank(groups = ValidationGroups.CreateExpectedTUReceipt.class)
    private String transportUnitBK;
    /** The name of the {@code TransportUnitType} the expected {@code TransportUnit} is of. */
    @JsonProperty("transportUnitTypeName")
    @NotBlank(groups = ValidationGroups.CreateExpectedTUReceipt.class)
    private String transportUnitTypeName;

    @JsonCreator
    ReceivingTransportUnitOrderPositionVO() {}

    @ConstructorProperties({"positionId", "transportUnitBK", "transportUnitTypeName"})
    public ReceivingTransportUnitOrderPositionVO(@NotNull Integer positionId,
                                                 @NotBlank(groups = ValidationGroups.CreateExpectedTUReceipt.class) String transportUnitBK,
                                                 @NotBlank(groups = ValidationGroups.CreateExpectedTUReceipt.class) String transportUnitTypeName) {
        super(positionId);
        this.transportUnitBK = transportUnitBK;
        this.transportUnitTypeName = transportUnitTypeName;
    }

    public String getTransportUnitBK() {
        return transportUnitBK;
    }

    public void setTransportUnitBK(@NotBlank(groups = ValidationGroups.CreateExpectedTUReceipt.class) String transportUnitBK) {
        this.transportUnitBK = transportUnitBK;
    }

    public String getTransportUnitTypeName() {
        return transportUnitTypeName;
    }

    public void setTransportUnitTypeName(@NotBlank(groups = ValidationGroups.CreateExpectedTUReceipt.class) String transportUnitTypeName) {
        this.transportUnitTypeName = transportUnitTypeName;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceivingTransportUnitOrderPositionVO)) return false;
        if (!super.equals(o)) return false;
        ReceivingTransportUnitOrderPositionVO that = (ReceivingTransportUnitOrderPositionVO) o;
        return Objects.equals(transportUnitBK, that.transportUnitBK) && Objects.equals(transportUnitTypeName, that.transportUnitTypeName);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), transportUnitBK, transportUnitTypeName);
    }

    @Override
    public void accept(BaseReceivingOrderPositionVOVisitor visitor) {
        visitor.visitVO(this);
    }
}
