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

import org.ameba.integration.jpa.ApplicationEntity;
import org.openwms.values.Problem;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.api.events.ReceivingOrderStateChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static javax.persistence.CascadeType.ALL;
import static org.openwms.wms.receiving.TimeProvider.DATE_TIME_WITH_TIMEZONE;

/**
 * A ReceivingOrder.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_ORDER",
        uniqueConstraints = {
            @UniqueConstraint(name = "UC_REC_ORDER_ID", columnNames = { "C_ORDER_ID" }),
        }
)
public class ReceivingOrder extends ApplicationEntity implements Serializable {

    /** Unique order id, business key. */
    @Column(name = "C_ORDER_ID", nullable = false)
    private String orderId;

    /** Current state of this order. */
    @Enumerated(EnumType.STRING)
    @Column(name = "C_ORDER_STATE")
    private OrderState orderState = OrderState.UNDEFINED;

    /**
     * Property to lock/unlock an order.
     * <ul>
     * <li>true: locked</li>
     * <li>false: unlocked</li>
     * </ul>
     */
    @Column(name = "C_LOCKED")
    private boolean locked = false;

    /** Current priority of the order. */
    @Column(name = "C_PRIORITY")
    private int priority;

    /** Latest date of this order can be processed. */
    @Column(name = "C_LATEST_DUE", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime latestDueDate;

    /** When the order is expected to be received. */
    @Column(name = "C_EXPECTED_RECEIPT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime expectedReceiptDate;

    /** Earliest date the order can be started. */
    @Column(name = "C_START_EARLIEST", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime earliestStartDate;

    /** Earliest date the order can be started. */
    @Column(name = "C_START_DATE", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime startDate;
    /** When the order has been finished. */

    @Column(name = "C_END_DATE", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime endDate;

    /** Date when the order should be allocated. */
    @Column(name = "C_NEXT_ALLOC", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime nextAllocationDate;

    /** Latest problem that is occurred on this order. */
    @Embedded
    private Problem problem;

    /** All ReceivingOrderPosition this order has. */
    @OneToMany(mappedBy = "order", cascade = {ALL}, fetch = FetchType.EAGER)
    @OrderBy("posNo")
    @Valid
    private List<BaseReceivingOrderPosition> positions = new ArrayList<>();

    /** Arbitrary detail information on this order, might by populated with ERP information. */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "WMS_REC_ORDER_DETAIL",
            joinColumns = {
                    @JoinColumn(name = "C_ORDER_PK", referencedColumnName = "C_PK")
            },
            foreignKey = @ForeignKey(name = "FK_DETAILS_RO")
    )
    @MapKeyColumn(name = "C_KEY")
    @Column(name = "C_VALUE")
    private Map<String, String> details;

    /*~ -------------- Constructors -------------- */
    /** Used by the JPA provider. */
    protected ReceivingOrder() {}

    public ReceivingOrder(String orderId) {
        this.orderId = orderId;
    }

    /*~ --------------- Lifecycle ---------------- */
    @PrePersist
    protected void prePersist() {
        this.orderState = OrderState.CREATED;
    }

    /*~ --------------- Accessors ---------------- */
    public String getOrderId() {
        return orderId;
    }

    void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean hasOrderId() {
        return this.orderId != null && !this.orderId.isEmpty();
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    public void changeOrderState(ApplicationEventPublisher eventPublisher, OrderState orderState) {
        eventPublisher.publishEvent(new ReceivingOrderStateChangeEvent(this, orderState));
        this.orderState = orderState;
    }

    public boolean isLocked() {
        return locked;
    }

    public int getPriority() {
        return priority;
    }

    public ZonedDateTime getLatestDueDate() {
        return latestDueDate;
    }

    public ZonedDateTime getExpectedReceiptDate() {
        return expectedReceiptDate;
    }

    public ZonedDateTime getEarliestStartDate() {
        return earliestStartDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public ZonedDateTime getNextAllocationDate() {
        return nextAllocationDate;
    }

    public Problem getProblem() {
        return problem;
    }

    public List<BaseReceivingOrderPosition> getPositions() {
        return positions == null ? Collections.emptyList() : positions;
    }

    public void setPositions(List<BaseReceivingOrderPosition> positions) {
        this.positions = positions;
    }

    public Map<String, String> getDetails() {
        return details == null ? new HashMap<>() : details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    /**
     * {@inheritDoc}
     *
     * Only the orderId.
     */
    @Override
    public String toString() {
        return orderId;
    }

    /**
     * {@inheritDoc}
     *
     * Only the orderId.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReceivingOrder that)) return false;
        if (!super.equals(o)) return false;
        return orderId.equals(that.orderId);
    }

    /**
     * {@inheritDoc}
     *
     * Only the orderId.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), orderId);
    }
}