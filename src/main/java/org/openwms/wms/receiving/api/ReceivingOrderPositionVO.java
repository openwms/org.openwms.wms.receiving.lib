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
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.receiving.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ReceivingOrderPositionVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReceivingOrderPositionVO extends BaseReceivingOrderPositionVO implements ConvertableVO, Serializable {

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

    @JsonCreator
    ReceivingOrderPositionVO() {}

    @ConstructorProperties({"positionId", "quantityExpected", "product"})
    public ReceivingOrderPositionVO(
            @NotNull Integer positionId,
            @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class) Measurable<?, ?, ?> quantityExpected,
            @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class) ProductVO product) {
        super(positionId);
        this.quantityExpected = quantityExpected;
        this.product = product;
    }

    public Measurable<?, ?, ?> getQuantityExpected() {
        return quantityExpected;
    }

    public void setQuantityExpected(@NotNull(groups = ValidationGroups.CreateQuantityReceipt.class) Measurable<?, ?, ?> quantityExpected) {
        this.quantityExpected = quantityExpected;
    }

    public Measurable<?, ?, ?> getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Measurable<?, ?, ?> quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public ProductVO getProduct() {
        return product;
    }

    public void setProduct(@NotNull(groups = ValidationGroups.CreateQuantityReceipt.class) ProductVO product) {
        this.product = product;
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
        if (!super.equals(o)) return false;
        ReceivingOrderPositionVO that = (ReceivingOrderPositionVO) o;
        return Objects.equals(quantityExpected, that.quantityExpected) && Objects.equals(quantityReceived, that.quantityReceived) && Objects.equals(product, that.product);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), quantityExpected, quantityReceived, product);
    }

    @Override
    public void accept(BaseReceivingOrderPositionVOVisitor visitor) {
        visitor.visitVO(this);
    }
}
