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

import org.ameba.exception.NotFoundException;
import org.ameba.system.ValidationUtil;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.ReceivingMessages;
import org.openwms.wms.receiving.ServiceProvider;
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.inventory.Product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static org.openwms.wms.order.OrderState.COMPLETED;

/**
 * A ReceivingOrderPosition is a persisted entity class that represents an expected receipt of {@code Product}s in a
 * particular quantity.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_ORDER_POS_PRODUCT")
public class ReceivingOrderPosition extends BaseReceivingOrderPosition implements Convertable, Serializable {

    /** The quantity that is expected to be receipt. */
    @org.hibernate.annotations.Type(type = "org.openwms.core.units.persistence.UnitUserType")
    @org.hibernate.annotations.Columns(columns = {
            @Column(name = "C_QTY_EXPECTED_TYPE"),
            @Column(name = "C_QTY_EXPECTED")
    })
    @NotNull(groups = ValidationGroups.CreateQuantityReceipt.class)
    private Measurable quantityExpected;

    /** The receipt quantity, this might be increased at arrival. */
    @org.hibernate.annotations.Type(type = "org.openwms.core.units.persistence.UnitUserType")
    @org.hibernate.annotations.Columns(columns = {
            @Column(name = "C_QTY_RECEIVED_TYPE"),
            @Column(name = "C_QTY_RECEIVED")
    })
    private Measurable quantityReceived;

    /** The expected {@link Product} to be receipt. */
    @ManyToOne
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
    public void setState(OrderState state) {
        if (state == COMPLETED) {
            this.quantityReceived = this.quantityExpected;
        }
        super.setState(state);
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
}