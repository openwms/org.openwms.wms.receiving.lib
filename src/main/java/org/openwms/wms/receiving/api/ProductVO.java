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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.ameba.annotation.Default;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.receiving.ValidationGroups;
import org.springframework.validation.annotation.Validated;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ProductVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProductVO implements Serializable {

    /** The foreign persistent key of the {@code Product}. */
    @JsonProperty("foreignPKey")
    private String foreignPKey;
    /** The product id is part of the unique business key. */
    @NotBlank(groups = ValidationGroups.Capture.class)
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
    private Measurable baseUnit;

    @JsonIgnore
    @Valid
    @Validated({ValidationGroups.Create.class,
            ValidationGroups.CreateBlindTUReceipt.class,
            ValidationGroups.CreateQuantityReceipt.class,
            ValidationGroups.CreateExpectedTUReceipt.class})
    public boolean isValid() {
        return ((sku != null && !sku.isEmpty()) ||
                (foreignPKey != null && !foreignPKey.isEmpty()));
    }

    @JsonCreator
    ProductVO() { }

    @Default
    @ConstructorProperties({"sku"})
    public ProductVO(@NotBlank String sku) {
        this.sku = sku;
    }

    public ProductVO(String sku, String foreignPKey) {
        if (sku != null && !sku.isEmpty()) {
            this.sku = sku;
        }
        if (foreignPKey != null && !foreignPKey.isEmpty()) {
            this.foreignPKey = foreignPKey;
        }
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

    public Measurable getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(Measurable baseUnit) {
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
