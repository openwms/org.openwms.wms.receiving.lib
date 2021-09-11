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
package org.openwms.wms.receiving.api.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.core.units.api.Measurable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A ReceivingOrderPositionMO.
 *
 * @author Heiko Scherrer
 */
public class ReceivingOrderPositionMO implements Serializable {

    /** The unique position ID within an ReceivingOrder - must not be empty. */
    @NotNull
    private Integer positionId;
    /** Current position state. Default is {@value} */
    private String state;
    /** The expected quantity of the expected product - must not be {@literal null}. */
    @NotNull
    private Measurable<?, ?, ?> quantityExpected;
    /** The already received quantity of the product. */
    private Measurable<?, ?, ?> quantityReceived;
    /** The unique SKU of the expected {@code Product} - must not be empty. */
    @NotNull
    private ProductMO product;
    /** Optional: How the position should be processed, manually oder automatically. */
    private String startMode;
    /** Optional: Expected receipts may also carry the unique identifier of the suppliers {@code TransportUnit}. */
    private String transportUnitBK;
    /** Optional: The suppliers type of {@code TransportUnit}. */
    private String transportUnitType;
    private String supplierPackingUnit;
    /** Arbitrary detail information on this position, might by populated with ERP information. */
    private Map<String, String> details;

    public Integer getPositionId() {
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

    public ProductMO getProduct() {
        return product;
    }

    public String getStartMode() {
        return startMode;
    }

    public String getTransportUnitBK() {
        return transportUnitBK;
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
    public String toString() {
        return String.valueOf(positionId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceivingOrderPositionMO that = (ReceivingOrderPositionMO) o;
        return Objects.equals(positionId, that.positionId) &&
                Objects.equals(state, that.state) &&
                Objects.equals(quantityExpected, that.quantityExpected) &&
                Objects.equals(quantityReceived, that.quantityReceived) &&
                Objects.equals(product, that.product) &&
                Objects.equals(startMode, that.startMode) &&
                Objects.equals(transportUnitBK, that.transportUnitBK) &&
                Objects.equals(transportUnitType, that.transportUnitType) &&
                Objects.equals(supplierPackingUnit, that.supplierPackingUnit) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionId, state, quantityExpected, quantityReceived, product, startMode, transportUnitBK, transportUnitType, supplierPackingUnit, details);
    }
}
