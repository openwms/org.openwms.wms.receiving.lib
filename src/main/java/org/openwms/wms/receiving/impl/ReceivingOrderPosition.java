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
package org.openwms.wms.receiving.impl;

import org.ameba.integration.jpa.BaseEntity;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.inventory.Product;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A ReceivingOrderPosition.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_ORDER_POSITION",
        uniqueConstraints = @UniqueConstraint(name = "UC_ORDER_ID_POS", columnNames = { "C_ORDER_ID", "C_POS_NO" }))
public class ReceivingOrderPosition extends BaseEntity implements Serializable {

    @ManyToOne(optional = false, targetEntity = ReceivingOrder.class)
    @JoinColumn(name = "C_ORDER_ID", referencedColumnName = "C_ORDER_ID", foreignKey = @ForeignKey(name = "FK_REC_POS_ORDER_ID"))
    private ReceivingOrder order;

    /** The position number is a unique index within a single {@link ReceivingOrder} instance. */
    @Column(name = "C_POS_NO", nullable = false)
    @NotNull
    private Integer posNo;

    /** Current position state. Default is {@value} */
    @Enumerated(EnumType.STRING)
    @Column(name = "C_STATE")
    @NotNull
    private OrderState state = OrderState.CREATED;

    @org.hibernate.annotations.Type(type = "org.openwms.core.units.persistence.UnitUserType")
    @org.hibernate.annotations.Columns(columns = {
            @Column(name = "C_QTY_EXPECTED_TYPE", nullable = false),
            @Column(name = "C_QTY_EXPECTED", nullable = false)
    })
    @NotNull
    private Measurable quantityExpected;

    @org.hibernate.annotations.Type(type = "org.openwms.core.units.persistence.UnitUserType")
    @org.hibernate.annotations.Columns(columns = {
            @Column(name = "C_QTY_RECEIVED_TYPE", nullable = false),
            @Column(name = "C_QTY_RECEIVED", nullable = false)
    })
    @NotNull
    private Measurable quantityReceived;

    /** The ordered {@link Product} identified by it's SKU. */
    @ManyToOne
    @JoinColumn(name = "C_SKU", referencedColumnName = "C_SKU", foreignKey = @ForeignKey(name = "FK_REC_POS_PRODUCT"), nullable = false)
    @NotNull
    private Product product;

    /** The business key of the expected {@code TransportUnit} that will be received. */
    @Column(name = "C_TRANSPORT_UNIT_BK")
    private String transportUnitBK;

    /** Some more detail information on this position, could by populated with ERP information. */
    @ElementCollection
    @CollectionTable(name = "WMS_REC_ORDER_POSITION_MAP",
            joinColumns = {
                    @JoinColumn(name = "C_ORDER_POS_PK", referencedColumnName = "C_PK")
            },
            foreignKey = @ForeignKey(name = "FK_DETAILS_ROP")
    )
    @MapKeyColumn(name = "C_KEY")
    @Column(name = "C_VALUE")
    private Map<String, String> details;

    /** Latest date this position can be processed. */
    @Column(name = "C_LATEST_DUE")
    private ZonedDateTime latestDueDate;

    /** Used by the JPA provider. */
    protected ReceivingOrderPosition() {}

    public String getTransportUnitBK() {
        return transportUnitBK;
    }

    public ReceivingOrder getOrder() {
        return order;
    }

    public int getPosNo() {
        return posNo;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
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
        this.quantityReceived = this.quantityReceived.add(quantityReceived);
        return quantityReceived;
    }

    public void setQuantityReceived(Measurable quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public Product getProduct() {
        return product;
    }

    /**
     * Get all the details of this {@link ReceivingOrderPosition}.
     *
     * @return As Map
     */
    public Map<String, String> getDetails() {
        return details == null ? Collections.emptyMap() : details;
    }

    /**
     * Add a new detail to the {@link ReceivingOrderPosition}.
     *
     * @param key The unique key of the detail
     * @param value The value as String
     * @return This instance
     */
    public ReceivingOrderPosition addDetail(String key, String value) {
        if (details == null) {
            details = new HashMap<>();
        }
        details.put(key, value);
        return this;
    }

    public ZonedDateTime getLatestDueDate() {
        return latestDueDate;
    }

    @Override
    public String toString() {
        return (order == null ? "n/a" : order.getOrderId()) + "/" + posNo;
    }
}