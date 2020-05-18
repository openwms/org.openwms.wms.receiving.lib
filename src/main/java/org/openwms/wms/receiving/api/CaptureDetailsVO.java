/*
 * Copyright 2005-2020 the original author or authors.
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
import org.openwms.core.units.api.DimensionUnit;
import org.openwms.core.units.api.Measurable;
import org.openwms.core.units.api.WeightUnit;

import java.io.Serializable;

/**
 * A CaptureDetailsVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CaptureDetailsVO implements Serializable {

    @JsonProperty("length")
    private Measurable<Long, ?, DimensionUnit> length;
    @JsonProperty("height")
    private Measurable<Long, ?, DimensionUnit> height;
    @JsonProperty("width")
    private Measurable<Long, ?, DimensionUnit> width;
    @JsonProperty("weight")
    private Measurable<Long, ?, WeightUnit> weight;

    public Measurable<Long, ?, DimensionUnit> getLength() {
        return length;
    }

    public void setLength(Measurable<Long, ?, DimensionUnit> length) {
        this.length = length;
    }

    public Measurable<Long, ?, DimensionUnit> getHeight() {
        return height;
    }

    public void setHeight(Measurable<Long, ?, DimensionUnit> height) {
        this.height = height;
    }

    public Measurable<Long, ?, DimensionUnit> getWidth() {
        return width;
    }

    public void setWidth(Measurable<Long, ?, DimensionUnit> width) {
        this.width = width;
    }

    public Measurable<Long, ?, WeightUnit> getWeight() {
        return weight;
    }

    public void setWeight(Measurable<Long, ?, WeightUnit> weight) {
        this.weight = weight;
    }
}
