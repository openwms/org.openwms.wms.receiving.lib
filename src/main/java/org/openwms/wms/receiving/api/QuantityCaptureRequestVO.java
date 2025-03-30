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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.receiving.ValidationGroups;

import java.io.Serializable;

/**
 * A QuantityCaptureRequestVO contains all information used to capture goods within a {@code LoadUnit} on top of a {@code TransportUnit}.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuantityCaptureRequestVO extends CaptureRequestVO implements Serializable {

    /** The captured {@code TransportUnit} where the goods are captured in. */
    @JsonProperty("transportUnit")
    @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class)
    private TransportUnitVO transportUnit;

    /** The unique identifier if the {@code LoadUnit} where the goods are captured in. */
    @JsonProperty("loadUnitLabel")
    @NotBlank(groups = ValidationGroups.CreateQuantityReceipt.class)
    private String loadUnitLabel;

    /** A type in case a {@code LoadUnit} needs to be created (optional). */
    @JsonProperty("loadUnitType")
    private String loadUnitType;

    /** The quantity that has been received during the capturing process. */
    @JsonProperty("quantity")
    @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class)
    private Measurable quantityReceived;

    /** The captured {@code Product}. */
    @JsonProperty("product")
    @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class)
    private ProductVO product;

    /** A serial number of the captured item (optional) . */
    @JsonProperty("serialNumber")
    private String serialNumber;

    /** The business key referring to an existing {@code Lot} (optional) . */
    @JsonProperty("lotId")
    private String lotId;

    public TransportUnitVO getTransportUnit() {
        return transportUnit;
    }

    public void setTransportUnit(TransportUnitVO transportUnit) {
        this.transportUnit = transportUnit;
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
