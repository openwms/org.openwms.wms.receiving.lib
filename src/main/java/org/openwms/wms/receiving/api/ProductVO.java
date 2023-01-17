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
package org.openwms.wms.receiving.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.wms.receiving.ValidationGroups;

import javax.measure.Quantity;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ProductVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductVO implements Serializable {

    /** The product id is part of the unique business key. */
    @NotEmpty(groups = ValidationGroups.Capture.class)
    @JsonProperty("sku")
    private String sku;
    /** An identifying label of the Product. */
    @JsonProperty("label")
    private String label;
    /** Textual descriptive text. */
    @JsonProperty("description")
    private String description;
    /** Products may be defined with different base units. */
    @JsonProperty("baseUnit")
    private Quantity<?> baseUnit;
    /** The foreign persistent key of the {@code Product}. */
    @JsonProperty("foreignPKey")
    private String foreignPKey;

    @JsonCreator
    ProductVO() {
    }

    public ProductVO(@NotEmpty String sku) {
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Quantity<?> getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(Quantity<?> baseUnit) {
        this.baseUnit = baseUnit;
    }

    public String getForeignPKey() {
        return foreignPKey;
    }

    public void setForeignPKey(String foreignPKey) {
        this.foreignPKey = foreignPKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVO)) return false;
        ProductVO productVO = (ProductVO) o;
        return Objects.equals(sku, productVO.sku) && Objects.equals(label, productVO.label) && Objects.equals(description, productVO.description) && Objects.equals(baseUnit, productVO.baseUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, label, description, baseUnit);
    }
}
