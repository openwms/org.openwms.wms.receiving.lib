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

import org.ameba.integration.jpa.ApplicationEntity;
import org.openwms.values.Problem;
import org.openwms.wms.order.OrderState;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

/**
 * A ReceivingOrder.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_ORDER",
        uniqueConstraints = @UniqueConstraint(name = "UC_ORDER_ID", columnNames = { "C_ORDER_ID" })
)
public class ReceivingOrder extends ApplicationEntity implements Serializable {

    /** Unique order id, business key. */
    @Column(name = "C_ORDER_ID", nullable = false)
    private String orderId;

    /** Current state of this Order. */
    @Enumerated(EnumType.STRING)
    @Column(name = "C_ORDER_STATE")
    private OrderState orderState = OrderState.UNDEFINED;

    /**
     * Property to lock/unlock an Order.
     * <ul>
     * <li>true: locked</li>
     * <li>false: unlocked</li>
     * </ul>
     * Default is {@value}
     */
    @Column(name = "C_LOCKED")
    private boolean locked = false;

    /** Current priority of the Order. */
    @Column(name = "C_PRIORITY")
    private int priority;

    /** Latest finish date of this Order. */
    @Column(name = "C_LATEST_DUE")
    private ZonedDateTime latestDueDate;

    /** Earliest date the Order can be started. */
    @Column(name = "C_START_DATE")
    private ZonedDateTime startDate;

    /** Date when the Order should be allocated. */
    @Column(name = "C_NEXT_ALLOC")
    private ZonedDateTime nextAllocationDate;

    /** Latest problem that is occurred on this Order. */
    @Embedded
    private Problem problem;

    /** All GoodsReceiptPosition this Order has. */
    @OneToMany(mappedBy = "order", cascade = {ALL})
    private Set<ReceivingOrderPosition> positions;

    /*~ -------------- Constructors -------------- */
    /** Used by the JPA provider. */
    protected ReceivingOrder() {}

    protected ReceivingOrder(String orderId) {
        this.orderId = orderId;
    }
    /*~ --------------- Lifecycle ---------------- */
    @PrePersist
    protected void prePersist() {
        this.orderState = OrderState.CREATED;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
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

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public ZonedDateTime getNextAllocationDate() {
        return nextAllocationDate;
    }

    public Problem getProblem() {
        return problem;
    }

    public Set<ReceivingOrderPosition> getPositions() {
        return positions == null ? Collections.emptySet() : positions;
    }

    @Override
    public String toString() {
        return orderId;
    }
}