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
package org.openwms.wms.receiving.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openwms.core.units.api.Measurable;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A QuantityCaptureOnLocationRequestVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuantityCaptureOnLocationRequestVO extends CaptureRequestVO implements Serializable {

    /** The Location where the captured material is located on. */
    @NotNull
    @JsonProperty("actualLocation")
    private LocationVO actualLocation;
    /** The quantity that has been received during the capturing Goods In process. */
    @NotNull
    @JsonProperty("quantity")
    private Measurable quantityReceived;
    /** The Product captured during the Goods In process. */
    @NotNull
    @JsonProperty("product")
    private ProductVO product;

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
}
