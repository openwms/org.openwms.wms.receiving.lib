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
package org.openwms.wms.receiving.inventory.events;

import org.openwms.core.units.api.Measurable;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * A InventoryProductMO is the representation of a {@code Product} in the WMS Inventory Service.
 *
 * @author Heiko Scherrer
 */
public class InventoryProductMO implements Serializable {

    private String pKey;
    private String sku;
    private String label;
    private Measurable baseUnit;
    private Boolean overbookingAllowed;
    private String description;

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Measurable getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(Measurable baseUnit) {
        this.baseUnit = baseUnit;
    }

    public Boolean getOverbookingAllowed() {
        return overbookingAllowed;
    }

    public void setOverbookingAllowed(Boolean overbookingAllowed) {
        this.overbookingAllowed = overbookingAllowed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventoryProductMO that)) return false;
        return Objects.equals(pKey, that.pKey) && Objects.equals(sku, that.sku) && Objects.equals(label, that.label) && Objects.equals(baseUnit, that.baseUnit) && Objects.equals(overbookingAllowed, that.overbookingAllowed) && Objects.equals(description, that.description);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(pKey, sku, label, baseUnit, overbookingAllowed, description);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", InventoryProductMO.class.getSimpleName() + "[", "]")
                .add("pKey='" + pKey + "'")
                .add("sku='" + sku + "'")
                .add("label='" + label + "'")
                .add("baseUnit=" + baseUnit)
                .add("overbookingAllowed=" + overbookingAllowed)
                .add("description='" + description + "'")
                .toString();
    }
}
