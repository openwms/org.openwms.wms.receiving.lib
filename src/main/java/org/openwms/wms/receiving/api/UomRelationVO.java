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

import java.beans.ConstructorProperties;

/**
 * A UomRelationVO defines and identifies an UOM for a given Product with an unique label. It is used to define the same Product in
 * various UOM, each one identified with a different label.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UomRelationVO {

    /** The persistent unique key. */
    @JsonProperty("pKey")
    public String pKey;

    /* For the Mapper */
    public UomRelationVO() {}

    @ConstructorProperties({"pKey"})
    public UomRelationVO(String pKey) {
        this.pKey = pKey;
    }
}
