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
package org.openwms.wms.receiving.transport;

import org.ameba.integration.jpa.ApplicationEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TransportUnit.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_TRANSPORT_UNIT", uniqueConstraints = {
        @UniqueConstraint(name = "UC_REC_TU_BARCODE", columnNames = {"C_BARCODE"}),
        @UniqueConstraint(name = "UC_REC_TU_FOREIGN_PID", columnNames = {"C_FOREIGN_PID"})
})
public class TransportUnit extends ApplicationEntity implements Serializable {

    /** The foreign persistent key of the {@code TransportUnit}. */
    @Column(name = "C_FOREIGN_PID", nullable = false)
    @NotEmpty
    private String foreignPKey;

    /** Unique natural key. */
    @Column(name = "C_BARCODE", nullable = false)
    @NotNull
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

    public String getForeignPKey() {
        return foreignPKey;
    }

    public void setForeignPKey(String foreignPKey) {
        this.foreignPKey = foreignPKey;
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

    /**
     * {@inheritDoc}
     *
     * Only the barcode.
     */
    @Override
    public String toString() {
        return barcode;
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportUnit that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(foreignPKey, that.foreignPKey) && Objects.equals(barcode, that.barcode) && Objects.equals(actualLocation, that.actualLocation);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), foreignPKey, barcode, actualLocation);
    }
}
