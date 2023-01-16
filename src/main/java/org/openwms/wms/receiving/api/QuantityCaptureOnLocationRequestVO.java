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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.core.units.api.Measurable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

import static org.openwms.wms.receiving.TimeProvider.DATE_TIME_WITH_TIMEZONE;

/**
 * A QuantityCaptureOnLocationRequestVO contains all information used to capture goods on a {@code Location}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuantityCaptureOnLocationRequestVO extends CaptureRequestVO implements Serializable {

    /** The {@code Location} where the captured items are located on. */
    @NotNull
    @JsonProperty("actualLocation")
    private LocationVO actualLocation;

    /** The quantity that has been received during the capturing process. */
    @NotNull
    @JsonProperty("quantity")
    private Measurable quantityReceived;

    /** The captured {@code Product}. */
    @JsonProperty("product")
    private ProductVO product;

    /** Describes the kind of UOM the {@code PackagingUnit}(s) are received in. */
    @JsonProperty("productUnit")
    public UomRelationVO uomRelation;

    /** A serial number of the captured item (optional). */
    @JsonProperty("serialNumber")
    private String serialNumber;

    /** The business key referring to an existing {@code Lot} (optional). */
    @JsonProperty("lotId")
    private String lotId;

    /** The expiration date of the particular {@code PackagingUnit}(s) (optional). */
    @JsonProperty("expiresAt")
    @JsonFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime expiresAt;

    /** The production date of the particular {@code PackagingUnit}(s) (optional). */
    @JsonProperty("productionDate")
    @JsonFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime productionDate;

    @JsonIgnore
    @Valid
    public boolean isValid() {
        return uomRelation != null || product != null;
    }

    public LocationVO getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(LocationVO actualLocation) {
        this.actualLocation = actualLocation;
    }

    public Measurable getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Measurable quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public ProductVO getProduct() {
        return product;
    }

    public void setProduct(ProductVO product) {
        this.product = product;
    }

    public UomRelationVO getUomRelation() {
        return uomRelation;
    }

    public boolean hasUomRelation() {
        return this.uomRelation != null;
    }

    public void setUomRelation(UomRelationVO uomRelation) {
        this.uomRelation = uomRelation;
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
}
