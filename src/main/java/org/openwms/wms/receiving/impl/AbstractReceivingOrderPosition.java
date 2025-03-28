/*
 * Copyright 2005-2025 the original author or authors.
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

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import org.ameba.integration.jpa.BaseEntity;
import org.openwms.wms.receiving.api.PositionState;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A AbstractReceivingOrderPosition.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractReceivingOrderPosition extends BaseEntity implements Serializable {

    @ManyToOne(optional = false, targetEntity = ReceivingOrder.class)
    @JoinColumn(name = "C_ORDER_ID", referencedColumnName = "C_ORDER_ID")
    private ReceivingOrder order;

    /** The position number is a unique index within a single {@link ReceivingOrder} instance. */
    @Column(name = "C_POS_NO", nullable = false)
    @NotNull
    private Integer posNo;

    /** Current position state. */
    @Enumerated(EnumType.STRING)
    @Column(name = "C_STATE")
    @NotNull
    private PositionState state = PositionState.CREATED;

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

    /** The name of the warehouses' LocationGroup where the {@code ReceivingOrderPosition} is expected to be received. */
    @Column(name = "C_EXPECTED_RECEIPT_AT")
    private String expectedReceiptWarehouse;

    /*~ -------------- Constructors -------------- */
    /** Used by the JPA provider. */
    protected AbstractReceivingOrderPosition() {}

    public AbstractReceivingOrderPosition(Integer posNo) {
        this.posNo = posNo;
    }

    /*~ --------------- Methods ---------------- */
    /**
     * Subclasses may validate themselves.
     *
     * @param validator The Validator instance
     * @param clazz The validation group
     */
    public abstract void validateOnCreation(Validator validator, Class<?> clazz);

    /**
     * Change the state of the position and publish a state change event if the new state has a higher ordinal value than the current state.
     * Additionally, recalculate the order state.
     *
     * @param eventPublisher The publisher to broadcast the state change event.
     * @param positionState The new state to which the position is transitioning.
     */
    public void changePositionState(ApplicationEventPublisher eventPublisher, PositionState positionState) {
        if (this.state.ordinal() < positionState.ordinal()) {
            eventPublisher.publishEvent(new ReceivingOrderPositionStateChangeEvent(this, positionState));
            this.state = positionState;
            this.order.recalculateOrderState(eventPublisher);
        }
    }

    /**
     * Capturing on an ReceivingOrderPosition is allowed if the state is any but not {@link PositionState#CANCELED}.
     *
     * @return if allowed
     */
    public boolean doesStateAllowCapturing() {
        return this.state == PositionState.CREATED || this.state == PositionState.PROCESSING
               || this.state == PositionState.PARTIALLY_COMPLETED || this.state == PositionState.COMPLETED;
    }

    /**
     * Returns a string representation of this AbstractReceivingOrderPosition instance, which includes the order ID and position number.
     *
     * @return A string containing the order ID (or "n/a" if the order is null) and the position number.
     */
    @Override
    public String toString() {
        return (order == null ? "n/a" : order.getOrderId()) + "/" + posNo;
    }

    /*~ --------------- Lifecycle ---------------- */
    /**
     * Subclasses have the chance for manipulation before creation.
     *
     * @param serviceProvider An instance that provides application services.
     */
    public void preCreate(ServiceProvider serviceProvider) {}

    /*~ --------------- Methods ---------------- */

    /**
     * {@inheritDoc}
     *
     * All fields without the details map.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractReceivingOrderPosition that = (AbstractReceivingOrderPosition) o;
        return Objects.equals(order, that.order) && Objects.equals(posNo, that.posNo) && state == that.state && Objects.equals(latestDueDate, that.latestDueDate) && Objects.equals(expectedReceiptWarehouse, that.expectedReceiptWarehouse);
    }

    /**
     * {@inheritDoc}
     *
     * All fields without the details map.
     */
    @Override
    public int hashCode() {
        return Objects.hash(order, posNo, state, latestDueDate, expectedReceiptWarehouse);
    }

    /*~ --------------- Accessors ---------------- */
    public ReceivingOrder getOrder() {
        return order;
    }

    public void setOrder(ReceivingOrder order) {
        this.order = order;
    }

    public int getPosNo() {
        return posNo;
    }

    public PositionState getState() {
        return state;
    }

    public void setState(PositionState state) {
        this.state = state;
    }

    /**
     * Get all the details of this {@link AbstractReceivingOrderPosition}.
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
     * Add a new detail to the {@link AbstractReceivingOrderPosition}.
     *
     * @param key The unique key of the detail
     * @param value The value as String
     * @return This instance
     */
    public AbstractReceivingOrderPosition addDetail(String key, String value) {
        getDetails().put(key, value);
        return this;
    }

    public ZonedDateTime getLatestDueDate() {
        return latestDueDate;
    }

    public void setLatestDueDate(ZonedDateTime latestDueDate) {
        this.latestDueDate = latestDueDate;
    }

    public String getExpectedReceiptWarehouse() {
        return expectedReceiptWarehouse;
    }

    public void setExpectedReceiptWarehouse(String expectedReceiptWarehouse) {
        this.expectedReceiptWarehouse = expectedReceiptWarehouse;
    }
}