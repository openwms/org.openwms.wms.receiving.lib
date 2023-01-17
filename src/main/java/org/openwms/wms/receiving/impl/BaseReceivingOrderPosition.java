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

import org.ameba.integration.jpa.BaseEntity;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.ServiceProvider;
import org.openwms.wms.receiving.api.events.ReceivingOrderPositionStateChangeEvent;
import org.springframework.context.ApplicationEventPublisher;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * A BaseReceivingOrderPosition.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "WMS_REC_ORDER_POSITION",
        uniqueConstraints = @UniqueConstraint(name = "UC_ORDER_ID_POS", columnNames = { "C_ORDER_ID", "C_POS_NO" }))
public abstract class BaseReceivingOrderPosition extends BaseEntity implements Serializable {

    @ManyToOne(optional = false, targetEntity = ReceivingOrder.class)
    @JoinColumn(name = "C_ORDER_ID", referencedColumnName = "C_ORDER_ID", foreignKey = @ForeignKey(name = "FK_REC_POS_ORDER_ID"))
    private ReceivingOrder order;

    /** The position number is a unique index within a single {@link ReceivingOrder} instance. */
    @Column(name = "C_POS_NO", nullable = false)
    @NotNull
    private Integer posNo;

    /** Current position state. */
    @Enumerated(EnumType.STRING)
    @Column(name = "C_STATE")
    @NotNull
    private OrderState state = OrderState.CREATED;

    /** Arbitrary detail information on this position, might be populated with ERP information. */
    @ElementCollection
    @CollectionTable(name = "WMS_REC_ORDER_POSITION_DETAIL",
            joinColumns = {
                    @JoinColumn(name = "C_ORDER_POS_PK", referencedColumnName = "C_PK")
            },
            foreignKey = @ForeignKey(name = "FK_REC_ORDER_DETAILS_ROP")
    )
    @MapKeyColumn(name = "C_KEY")
    @Column(name = "C_VALUE")
    private Map<String, String> details;

    /** Latest date this position can be processed. */
    @Column(name = "C_LATEST_DUE")
    private ZonedDateTime latestDueDate;

    /** Used by the JPA provider. */
    protected BaseReceivingOrderPosition() {}

    public BaseReceivingOrderPosition(Integer posNo) {
        this.posNo = posNo;
    }

    /**
     * Subclasses may validate themselves.
     *
     * @param validator The Validator instance
     * @param clazz The validation group
     */
    public abstract void validateOnCreation(Validator validator, Class<?> clazz);

    /**
     * Subclasses have the chance for manipulation before creation.
     *
     * @param serviceProvider An instance that provides application services.
     */
    public void preCreate(ServiceProvider serviceProvider) {};

    public ReceivingOrder getOrder() {
        return order;
    }

    public void setOrder(ReceivingOrder order) {
        this.order = order;
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

    public void changeOrderState(ApplicationEventPublisher eventPublisher, OrderState orderState) {
        eventPublisher.publishEvent(new ReceivingOrderPositionStateChangeEvent(this, orderState));
        this.state = orderState;
    }

    /**
     * Get all the details of this {@link BaseReceivingOrderPosition}.
     *
     * @return As Map
     */
    public Map<String, String> getDetails() {
        if (details == null) {
            details = new HashMap<>();
        }
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    /**
     * Add a new detail to the {@link BaseReceivingOrderPosition}.
     *
     * @param key The unique key of the detail
     * @param value The value as String
     * @return This instance
     */
    public BaseReceivingOrderPosition addDetail(String key, String value) {
        getDetails().put(key, value);
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