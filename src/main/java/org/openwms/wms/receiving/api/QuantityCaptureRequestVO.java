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
package org.openwms.wms.receiving.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.measure.Quantity;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A QuantityCaptureRequestVO contains all information used to capture goods within a {@code LoadUnit} on top of a {@code TransportUnit}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuantityCaptureRequestVO extends CaptureRequestVO implements Serializable {

    /** The business key of the captured {@code TransportUnit} where the goods are captured in. */
    @NotEmpty
    @JsonProperty("transportUnitBK")
    private String transportUnitId;

    /** The unique identifier if the {@code LoadUnit} where the goods are captured in. */
    @NotEmpty
    @JsonProperty("loadUnitLabel")
    private String loadUnitLabel;

    /** A type in case a {@code LoadUnit} needs to be created (optional). */
    @JsonProperty("loadUnitType")
    private String loadUnitType;

    /** The quantity that has been received during the capturing process. */
    @NotNull
    @JsonProperty("quantity")
    private Quantity<?> quantityReceived;

    /** The captured {@code Product}. */
    @NotNull
    @JsonProperty("product")
    private ProductVO product;

    /** A serial number of the captured item (optional) . */
    @JsonProperty("serialNumber")
    private String serialNumber;

    /** The business key referring to an existing {@code Lot} (optional) . */
    @JsonProperty("lotId")
    private String lotId;

    public String getTransportUnitId() {
        return transportUnitId;
    }

    public void setTransportUnitId(String transportUnitId) {
        this.transportUnitId = transportUnitId;
    }

    public String getLoadUnitLabel() {
        return loadUnitLabel;
    }

    public void setLoadUnitLabel(String loadUnitLabel) {
        this.loadUnitLabel = loadUnitLabel;
    }

    public String getLoadUnitType() {
        return loadUnitType;
    }

    public void setLoadUnitType(String loadUnitType) {
        this.loadUnitType = loadUnitType;
    }

    public Quantity<?> getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Quantity<?> quantityReceived) {
        this.quantityReceived = quantityReceived;
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
}
