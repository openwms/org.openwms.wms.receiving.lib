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
package org.openwms.wms.receiving.impl;

import org.ameba.integration.jpa.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * A ReceivingOrderPositionDetails.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_ORDER_POSITION_DETAILS")
public class ReceivingOrderPositionDetails extends BaseEntity implements Serializable {

    @Column(name = "C_TRANSPORT_UNIT_TYPE")
    private String transportUnitType;

    @Column(name = "C_HEIGHT")
    private Integer height;

    @Column(name = "C_WIDTH")
    private Integer width;

    @Column(name = "C_LENGTH")
    private Integer length;

    public String getTransportUnitType() {
        return transportUnitType;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeightIfExists(Consumer<Integer> consumer) {
        if (this.height != null) {
            consumer.accept(this.height);
        }
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidthIfExists(Consumer<Integer> consumer) {
        if (this.width != null) {
            consumer.accept(this.width);
        }
    }
    public Integer getLength() {
        return length;
    }

    public void setLengthIfExists(Consumer<Integer> consumer) {
        if (this.length != null) {
            consumer.accept(this.length);
        }
    }
}
