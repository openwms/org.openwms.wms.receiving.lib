/*
 * Copyright 2005-2022 the original author or authors.
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;
import org.openwms.core.units.api.Weight;
import org.openwms.wms.receiving.api.CaptureDetailsVO;
import org.openwms.wms.receiving.api.LocationVO;
import org.openwms.wms.receiving.api.UomRelationVO;

import javax.measure.Quantity;
import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;

import static org.openwms.wms.receiving.TimeProvider.DATE_TIME_WITH_TIMEZONE;

/**
 * A PackagingUnitVO represents a quantity of a {@code Product} packaged into a single physical unit.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PackagingUnitVO extends AbstractBase<PackagingUnitVO> implements Serializable {

    /** Ordered Product. */
    @JsonProperty("product")
    @NotNull
    private ProductVO product;
    /** The type of PackagingUnit. */
    @JsonProperty("uomRelation")
    public UomRelationVO uomRelation;
    /** An optional serial number of the {@code PackagingUnit}. */
    @JsonProperty("serialNumber")
    private String serialNumber;
    /** The business key referring to a defined {@code Lot}. */
    @JsonProperty("lotId")
    private String lotId;
    /** The expiration date of this particular {@code PackagingUnit}. */
    @JsonProperty("expiresAt")
    @JsonFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime expiresAt;
    /** The production date of this particular {@code PackagingUnit}. */
    @JsonProperty("productionDate")
    @JsonFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime productionDate;
    /** Quantity ordered. */
    @JsonProperty("quantity")
    @NotNull
    private Quantity<?> quantity;
    /** The actualLocation the {@code PackagingUnit} is placed on. */
    @JsonProperty("actualLocation")
    public LocationVO actualLocation;
    /** The current length of the {@code PackagingUnit}. */
    @JsonProperty("length")
    private Integer length;
    /** The current width of the {@code PackagingUnit}. */
    @JsonProperty("width")
    private Integer width;
    /** The current height of the {@code PackagingUnit}. */
    @JsonProperty("height")
    private Integer height;
    /** The current weight of the {@code PackagingUnit}. */
    @JsonProperty("weight")
    private Weight weight;
    /** Any kind of message placed on the {@code PackagingUnit}. */
    @JsonProperty("message")
    private String message;

    public PackagingUnitVO() { }

    @ConstructorProperties({"product", "quantity"})
    public PackagingUnitVO(ProductVO product, Quantity<?> quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    @ConstructorProperties({"productUnit", "quantity"})
    public PackagingUnitVO(UomRelationVO uomRelation, Quantity<?> quantity) {
        this.uomRelation = uomRelation;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(ZonedDateTime productionDate) {
        this.productionDate = productionDate;
    }

    public Quantity<?> getQuantity() {
        return quantity;
    }

    public void setQuantity(Quantity<?> quantity) {
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

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", PackagingUnitVO.class.getSimpleName() + "[", "]")
                .add("product=" + product)
                .add("serialNumber='" + serialNumber + "'")
                .add("lot='" + lotId + "'")
                .add("quantity=" + quantity)
                .add("actualLocation=" + actualLocation)
                .add("length=" + length)
                .add("width=" + width)
                .add("height=" + height)
                .add("weight=" + weight)
                .add("message='" + message + "'")
                .toString();
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PackagingUnitVO)) return false;
        if (!super.equals(o)) return false;
        PackagingUnitVO that = (PackagingUnitVO) o;
        return Objects.equals(product, that.product) && Objects.equals(serialNumber, that.serialNumber) && Objects.equals(lotId, that.lotId) && Objects.equals(quantity, that.quantity) && Objects.equals(actualLocation, that.actualLocation) && Objects.equals(length, that.length) && Objects.equals(width, that.width) && Objects.equals(height, that.height) && Objects.equals(weight, that.weight) && Objects.equals(message, that.message);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), product, serialNumber, lotId, quantity, actualLocation, length, width, height, weight, message);
    }
}
