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
package org.openwms.wms.receiving.inventory;

import org.ameba.integration.jpa.ApplicationEntity;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.openwms.core.units.UnitConstants;
import org.openwms.core.units.api.Measurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Product.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_PRODUCT",
        uniqueConstraints = {
                @UniqueConstraint(name = "UC_REC_PRODUCT_SKU", columnNames = {"C_SKU"}),
                @UniqueConstraint(name = "UC_REC_PRODUCT_FOREIGN_PID", columnNames = {"C_FOREIGN_PID"})
        }
)
public class Product extends ApplicationEntity implements Comparable<Product>, Serializable {

    /** The foreign persistent key of the {@code Product}. */
    @Column(name = "C_FOREIGN_PID", nullable = false)
    @NotBlank
    private String foreignPKey;

    /** The product id is part of the unique business key. */
    @Column(name = "C_SKU")
    @NotBlank
    private String sku;

    /** An identifying label of the Product. */
    @Column(name = "C_LABEL")
    private String label;

    /** Textual descriptive text. */
    @Column(name = "C_DESCRIPTION")
    private String description;

    /** Products may be defined with different base units. */
    @Type(type = "org.openwms.core.units.persistence.UnitUserType")
    @Columns(columns = {
            @Column(name = "C_BASE_UNIT_TYPE", nullable = false),
            @Column(name = "C_BASE_UNIT_QTY", length = UnitConstants.QUANTITY_LENGTH, nullable = false)
    })
    //problems when mapping
    //@NotNull
    private Measurable baseUnit;

    @Column(name = "C_OVERBOOKING_ALLOWED", nullable = false)
    @NotNull
    private Boolean overbookingAllowed;

    /** Dear JPA ... */
    protected Product() {
    }

    /**
     * Create a Product with a sku.
     *
     * @param sku The sku
     */
    public Product(String sku) {
        this.sku = sku;
    }

    public String getForeignPKey() {
        return foreignPKey;
    }

    public void setForeignPKey(String foreignPKey) {
        this.foreignPKey = foreignPKey;
    }

    /**
     * Get the SKU.
     * 
     * @return the SKU.
     */
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Measurable getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(Measurable baseUnit) {
        this.baseUnit = baseUnit;
    }

    public Boolean getOverbookingAllowed() {
        return overbookingAllowed;
    }

    public void setOverbookingAllowed(Boolean overbookingAllowed) {
        this.overbookingAllowed = overbookingAllowed;
    }

    /**
     * {@inheritDoc}
     *
     * Uses the sku for comparison
     */
    @Override
    public int compareTo(Product o) {
        return null == o ? -1 : this.sku.compareTo(o.sku);
    }

    /**
     * {@inheritDoc}
     *
     * Use all fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        if (!super.equals(o)) return false;
        return overbookingAllowed == product.overbookingAllowed && Objects.equals(foreignPKey, product.foreignPKey) && Objects.equals(sku, product.sku) && Objects.equals(label, product.label) && Objects.equals(description, product.description) && Objects.equals(baseUnit, product.baseUnit);
    }

    /**
     * {@inheritDoc}
     *
     * Use all fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), foreignPKey, sku, label, description, baseUnit, overbookingAllowed);
    }

    /**
     * {@inheritDoc}
     * 
     * Return the SKU;
     */
    @Override
    public String toString() {
        return sku;
    }

    /**
     * Return the short identification of this Product.
     *
     * @return The SKU
     */
    public String shortId() {
        return sku;
    }
}