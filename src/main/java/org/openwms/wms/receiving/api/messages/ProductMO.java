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
package org.openwms.wms.receiving.api.messages;

import org.openwms.core.units.api.Measurable;

import java.io.Serializable;
import java.util.Objects;

/**
 * A ProductMO.
 *
 * @author Heiko Scherrer
 */
public class ProductMO implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMO)) return false;
        ProductMO productMO = (ProductMO) o;
        return Objects.equals(pKey, productMO.pKey) && Objects.equals(sku, productMO.sku) && Objects.equals(label, productMO.label) && Objects.equals(baseUnit, productMO.baseUnit) && Objects.equals(description, productMO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pKey, sku, label, baseUnit, description);
    }

    @Override
    public String toString() {
        return "ProductMO{" +
                "pKey='" + pKey + '\'' +
                ", sku='" + sku + '\'' +
                ", label='" + label + '\'' +
                ", baseUnit=" + baseUnit +
                ", description='" + description + '\'' +
                '}';
    }
}
