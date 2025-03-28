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
package org.openwms.wms.receiving.impl;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.ameba.exception.NotFoundException;
import org.ameba.system.ValidationUtil;
import org.hibernate.annotations.CompositeType;
import org.openwms.core.units.UnitConstants;
import org.openwms.core.units.api.Measurable;
import org.openwms.core.units.persistence.UnitUserType;
import org.openwms.wms.receiving.ReceivingMessages;
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.api.PositionState;
import org.openwms.wms.receiving.inventory.Product;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;
import java.util.Objects;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

/**
 * A ReceivingOrderPosition is a persisted entity class that represents an expected receipt of {@code Product}s in a
 * particular quantity.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// Bug in hibernate prevents overriding fkcontraints
//@AssociationOverride(name = "order", joinColumns =
//    @JoinColumn(name = "C_ORDER_ID", referencedColumnName = "C_ORDER_ID", foreignKey = @ForeignKey(name = "FK_REC_POS_ORDER_ID_PROD")))
// under observation: seems to be fixed with upgrade to SpringBoot 3.4.1
@Table(
        name = "WMS_REC_ORDER_POS_PRODUCT",
        uniqueConstraints = @UniqueConstraint(name = "UC_ORDER_ID_POS", columnNames = { "C_ORDER_ID", "C_POS_NO" })
)
public class ReceivingOrderPosition extends AbstractReceivingOrderPosition implements Convertable, Serializable {

    /** The quantity that is expected to be receipt. */
    @CompositeType(UnitUserType.class)
    @AttributeOverride(name = "magnitude", column = @Column(name = "C_QTY_EXPECTED", length = UnitConstants.QUANTITY_LENGTH, nullable = false))
    @AttributeOverride(name = "unitType", column = @Column(name = "C_QTY_EXPECTED_TYPE", nullable = false))
    @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class)
    private Measurable<?,?,?> quantityExpected;

    /** The receipt quantity, this might be increased at arrival. */
    @CompositeType(UnitUserType.class)
    @AttributeOverride(name = "magnitude", column = @Column(name = "C_QTY_RECEIVED", length = UnitConstants.QUANTITY_LENGTH))
    @AttributeOverride(name = "unitType", column = @Column(name = "C_QTY_RECEIVED_TYPE"))
    private Measurable<?,?,?> quantityReceived;

    /** The expected {@link Product} to be receipt. */
    @ManyToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "C_SKU", referencedColumnName = "C_SKU", foreignKey = @ForeignKey(name = "FK_REC_POS_PRODUCT"), nullable = false)
    @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class)
    private Product product;

    /** Used by the JPA provider. */
    protected ReceivingOrderPosition() {}

    @Override
    public void validateOnCreation(Validator validator, Class<?> clazz) {
        if (clazz.isAssignableFrom(ValidationGroups.Create.class)) {
            ValidationUtil.validate(validator, this, ValidationGroups.CreateQuantityReceipt.class);
        }
        validator.validate(this, clazz);
    }

    @Override
    public void preCreate(ServiceProvider serviceProvider) {
        if (this.hasProduct()) {
            this.setProduct(getProduct(serviceProvider, this.getProduct().getSku()));
        }
    }

    private Product getProduct(ServiceProvider serviceProvider, String sku) {
        return serviceProvider.getProductService().findBySku(sku).orElseThrow(
                () -> new NotFoundException(
                        serviceProvider.getTranslator(),
                        ReceivingMessages.PRODUCT_NOT_FOUND,
                        sku
                ));
    }

    public ReceivingOrderPosition(Integer posNo, Measurable quantityExpected, Product product) {
        super(posNo);
        this.quantityExpected = quantityExpected;
        this.product = product;
    }

    /**
     * {@inheritDoc}
     *
     * If the state is set to {@code COMPLETED} the {@code quantityReceived} is set to {@code quantityExpected}.
     */
    @Override
    public void changePositionState(ApplicationEventPublisher eventPublisher, PositionState positionState) {
        super.changePositionState(eventPublisher, positionState);
        if (positionState == PositionState.COMPLETED) {
            this.quantityReceived = this.quantityExpected;
        }
    }

    public Measurable getQuantityExpected() {
        return quantityExpected;
    }

    public void setQuantityExpected(Measurable quantityExpected) {
        this.quantityExpected = quantityExpected;
    }

    public Measurable getQuantityReceived() {
        return quantityReceived;
    }

    public Measurable addQuantityReceived(Measurable quantityReceived) {
        if (this.quantityReceived == null) {
            this.quantityReceived = quantityReceived;
        } else {
            this.quantityReceived = this.quantityReceived.add(quantityReceived);
        }
        return this.quantityReceived;
    }

    public void setQuantityReceived(Measurable quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public Product getProduct() {
        return product;
    }

    public boolean hasProduct() {
        return product != null;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public void accept(BaseReceivingOrderPositionVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReceivingOrderPosition that = (ReceivingOrderPosition) o;
        return Objects.equals(quantityExpected, that.quantityExpected) && Objects.equals(quantityReceived, that.quantityReceived) && Objects.equals(product, that.product);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), quantityExpected, quantityReceived, product);
    }
}