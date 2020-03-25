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
package org.openwms.wms.transport;

import org.ameba.integration.jpa.ApplicationEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * A TransportUnit.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_TRANSPORT_UNIT", uniqueConstraints = @UniqueConstraint(name = "UC_REC_TRANSPORT_UNIT_BARCODE", columnNames = {"C_BARCODE"}))
public class TransportUnit extends ApplicationEntity implements Serializable {

    /** Unique natural key. */
    @Column(name = "C_BARCODE", nullable = false)
    private String barcode;

    /** The current {@code Location} of the {@code TransportUnit}. */
    @Column(name = "C_ACTUAL_LOCATION", nullable = false)
    private String actualLocation;

    /** Dear JPA... */
    protected TransportUnit() {
    }

    /** Dear JPA... */
    public TransportUnit(String barcode, String actualLocation) {
        this.barcode = barcode;
        this.actualLocation = actualLocation;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getActualLocation() {
        return actualLocation;
    }

    public void setActualLocation(String actualLocation) {
        this.actualLocation = actualLocation;
    }

    @Override
    public String toString() {
        return barcode;
    }
}
