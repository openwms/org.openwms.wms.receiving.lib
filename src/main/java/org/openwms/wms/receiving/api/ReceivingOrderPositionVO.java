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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.core.units.api.Measurable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    @NotEmpty
    @JsonProperty("positionId")
    private String positionId;
    /** Current position state. Default is {@value} */
    @JsonProperty("state")
    private String state;
    /** The expected quantity of the expected product - must not be {@literal null}. */
    @NotNull
    @JsonProperty("quantityExpected")
    private Measurable<?, ?, ?> quantityExpected;
    /** The already received quantity of the product. */
    @JsonProperty("quantityReceived")
    private Measurable<?, ?, ?> quantityReceived;
    /** The unique SKU of the expected {@code Product} - must not be empty. */
    @NotNull
    @JsonProperty("product")
    private ProductVO product;
    /** Optional: How the position should be processed, manually oder automatically. */
    @JsonProperty("startMode")
    private String startMode;
    /** Optional: Expected receipts may also carry the unique identifier of the suppliers {@code TransportUnit}. */
    @JsonProperty("transportUnitId")
    private String transportUnitId;
    /** Optional: The suppliers type of {@code TransportUnit}. */
    @JsonProperty("transportUnitType")
    private String transportUnitType;
    @JsonProperty("supplierPackingUnit")
    private String supplierPackingUnit;
    @JsonProperty("details")
    private Map<String, String> details;

    @JsonCreator
    ReceivingOrderPositionVO() {
    }

    public ReceivingOrderPositionVO(@NotEmpty String positionId, @NotEmpty Measurable<?, ?, ?> quantityExpected, @NotNull ProductVO product) {
        this.positionId = positionId;
        this.quantityExpected = quantityExpected;
        this.product = product;
    }

    public String getPositionId() {
        return positionId;
    }

    public Measurable<?, ?, ?> getQuantityExpected() {
        return quantityExpected;
    }

    public Measurable<?, ?, ?> getQuantityReceived() {
        return quantityReceived;
    }

    public String getState() {
        return state;
    }

    public ProductVO getProduct() {
        return product;
    }

    public String getStartMode() {
        return startMode;
    }

    public String getTransportUnitId() {
        return transportUnitId;
    }

    public String getTransportUnitType() {
        return transportUnitType;
    }

    public String getSupplierPackingUnit() {
        return supplierPackingUnit;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceivingOrderPositionVO that = (ReceivingOrderPositionVO) o;
        return Objects.equals(positionId, that.positionId) &&
                Objects.equals(state, that.state) &&
                Objects.equals(quantityExpected, that.quantityExpected) &&
                Objects.equals(quantityReceived, that.quantityReceived) &&
                Objects.equals(product, that.product) &&
                Objects.equals(startMode, that.startMode) &&
                Objects.equals(transportUnitId, that.transportUnitId) &&
                Objects.equals(transportUnitType, that.transportUnitType) &&
                Objects.equals(supplierPackingUnit, that.supplierPackingUnit) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionId, state, quantityExpected, quantityReceived, product, startMode, transportUnitId, transportUnitType, supplierPackingUnit, details);
    }
}
