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
package org.openwms.wms.inventory.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;
import org.openwms.core.units.api.Measurable;
import org.openwms.core.units.api.Weight;
import org.openwms.wms.receiving.api.CaptureDetailsVO;
import org.openwms.wms.receiving.api.LocationVO;

import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PackagingUnitVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackagingUnitVO extends AbstractBase<PackagingUnitVO> implements Serializable {

    /** Ordered Product. */
    @JsonProperty("product")
    @NotNull
    private ProductVO product;
    /** Quantity ordered. */
    @JsonProperty("quantity")
    @NotNull
    private Measurable quantity;
    /** The actualLocation the PackagingUnit is placed on. */
    @JsonProperty("actualLocation")
    public LocationVO actualLocation;
    /** The current length of the PackagingUnit. */
    @JsonProperty("length")
    private Integer length;
    /** The current width of the PackagingUnit. */
    @JsonProperty("width")
    private Integer width;
    /** The current height of the PackagingUnit. */
    @JsonProperty("height")
    private Integer height;
    /** The current weight of the PackagingUnit. */
    @JsonProperty("weight")
    private Weight weight;
    /** Any kind of message placed on the PackagingUnit. */
    @JsonProperty("message")
    private String message;

    public PackagingUnitVO() { }

    @ConstructorProperties({"product", "quantity"})
    public PackagingUnitVO(ProductVO product, Measurable quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public void setDetails(CaptureDetailsVO details) {
        if (details != null) {
            this.setLength(details.getLength());
            this.setWidth(details.getWidth());
            this.setHeight(details.getHeight());
            if (details.getWeight() != null) {
                this.setWeight(details.getWeight());
            }
            this.setMessage(details.getMessageText());
        }
    }

    public ProductVO getProduct() {
        return product;
    }

    public void setProduct(ProductVO product) {
        this.product = product;
    }

    public Measurable getQuantity() {
        return quantity;
    }

    public void setQuantity(Measurable quantity) {
        this.quantity = quantity;
    }

    public LocationVO getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(LocationVO actualLocation) {
        this.actualLocation = actualLocation;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PackagingUnitVO{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackagingUnitVO that = (PackagingUnitVO) o;
        return Objects.equals(product, that.product) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(length, that.length) &&
                Objects.equals(width, that.width) &&
                Objects.equals(height, that.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, length, width, height);
    }
}
