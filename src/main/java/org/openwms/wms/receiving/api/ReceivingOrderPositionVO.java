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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.receiving.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A ReceivingOrderPositionVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReceivingOrderPositionVO implements Serializable {

    /** The unique position ID within an ReceivingOrder - must not be empty. */
    @NotNull
    @JsonProperty("positionId")
    private Integer positionId;
    /** Current position state. Default is {@value} */
    @JsonProperty("state")
    private String state;
    /** The expected quantity of the expected product - must not be {@literal null}. */
    @JsonProperty("quantityExpected")
    @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class)
    private Measurable<?, ?, ?> quantityExpected;
    /** The already received quantity of the product. */
    @JsonProperty("quantityReceived")
    private Measurable<?, ?, ?> quantityReceived;
    /** The unique SKU of the expected {@code Product} - must not be empty. */
    @JsonProperty("product")
    @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class)
    private ProductVO product;
    /** Optional: How the position should be processed, manually oder automatically. */
    @JsonProperty("startMode")
    private String startMode;
    /** Optional: Expected receipts may also carry the unique identifier of the suppliers {@code TransportUnit}. */
    @JsonProperty("transportUnitBK")
    @NotNull(groups = ValidationGroups.CreateExpectedTUReceipt.class)
    private String transportUnitBK;
    /** The name of the {@code TransportUnitType} the expected {@code TransportUnit} is of. */
    @JsonProperty("transportUnitTypeName")
    @NotNull(groups = ValidationGroups.CreateExpectedTUReceipt.class)
    private String transportUnitTypeName;
    @JsonProperty("details")
    private Map<String, String> details;

    @JsonCreator
    ReceivingOrderPositionVO() {}

    @ConstructorProperties("positionId")
    public ReceivingOrderPositionVO(@NotNull Integer positionId) {
        this.positionId = positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public Measurable<?, ?, ?> getQuantityExpected() {
        return quantityExpected;
    }

    public void setQuantityExpected(Measurable<?, ?, ?> quantityExpected) {
        this.quantityExpected = quantityExpected;
    }

    public Measurable<?, ?, ?> getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Measurable<?, ?, ?> quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ProductVO getProduct() {
        return product;
    }

    public void setProduct(ProductVO product) {
        this.product = product;
    }

    public String getStartMode() {
        return startMode;
    }

    public void setStartMode(String startMode) {
        this.startMode = startMode;
    }

    public String getTransportUnitBK() {
        return transportUnitBK;
    }

    public void setTransportUnitBK(String transportUnitBK) {
        this.transportUnitBK = transportUnitBK;
    }

    public String getTransportUnitTypeName() {
        return transportUnitTypeName;
    }

    public void setTransportUnitTypeName(String transportUnitTypeName) {
        this.transportUnitTypeName = transportUnitTypeName;
    }

    public Map<String, String> getDetails() {
        if (details == null) {
            details = new HashMap<>();
        }
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return String.valueOf(positionId);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceivingOrderPositionVO)) return false;
        ReceivingOrderPositionVO that = (ReceivingOrderPositionVO) o;
        return Objects.equals(positionId, that.positionId) && Objects.equals(state, that.state) && Objects.equals(quantityExpected, that.quantityExpected) && Objects.equals(quantityReceived, that.quantityReceived) && Objects.equals(product, that.product) && Objects.equals(startMode, that.startMode) && Objects.equals(transportUnitBK, that.transportUnitBK) && Objects.equals(transportUnitTypeName, that.transportUnitTypeName) && Objects.equals(details, that.details);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(positionId, state, quantityExpected, quantityReceived, product, startMode, transportUnitBK, transportUnitTypeName, details);
    }
}
