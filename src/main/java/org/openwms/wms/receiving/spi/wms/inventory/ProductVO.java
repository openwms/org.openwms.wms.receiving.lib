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
package org.openwms.wms.receiving.spi.wms.inventory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;
import org.openwms.core.units.api.Measurable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ProductVO that is a representation and mapped to the Product like it is defined in the WMS Inventory Service API.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class ProductVO extends AbstractBase<ProductVO> implements Serializable {

    interface Load {}

    /** The persistent unique key. */
    @JsonProperty("pKey")
    @NotBlank(groups = Load.class)
    private String pKey;
    /** The product id is part of the unique business key. */
    @JsonProperty("sku")
    @NotBlank(groups = Load.class)
    private String sku;
    /** An identifying label of the Product. */
    @JsonProperty("label")
    @NotBlank(groups = Load.class)
    private String label;
    /** Is it allowed to receive a higher quantity as expected/announced of this Product? */
    @JsonProperty("overbookingAllowed")
    @NotNull(groups = Load.class)
    private Boolean overbookingAllowed;
    /** {@code Product}s may be defined with different base units. */
    @JsonProperty("baseUnit")
    @NotNull(groups = Load.class)
    private Measurable baseUnit;
    /** Textual descriptive text. */
    @JsonProperty("description")
    private String description;

    public ProductVO() { }

    private ProductVO(Builder builder) {
        setpKey(builder.pKey);
        setSku(builder.sku);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

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

    public Boolean getOverbookingAllowed() {
        return overbookingAllowed;
    }

    public void setOverbookingAllowed(Boolean overbookingAllowed) {
        this.overbookingAllowed = overbookingAllowed;
    }

    public Measurable getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(Measurable baseUnit) {
        this.baseUnit = baseUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return sku;
    }

    public static final class Builder {
        private String pKey;
        private String sku;

        private Builder() {
        }

        public Builder pKey(String val) {
            pKey = val;
            return this;
        }

        public Builder sku(String val) {
            sku = val;
            return this;
        }

        public ProductVO build() {
            return new ProductVO(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVO)) return false;
        if (!super.equals(o)) return false;
        var productVO = (ProductVO) o;
        return Objects.equals(pKey, productVO.pKey) && Objects.equals(sku, productVO.sku) && Objects.equals(label, productVO.label) && Objects.equals(overbookingAllowed, productVO.overbookingAllowed && Objects.equals(baseUnit, productVO.baseUnit) && Objects.equals(description, productVO.description));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, sku, label, overbookingAllowed, baseUnit, description);
    }
}
