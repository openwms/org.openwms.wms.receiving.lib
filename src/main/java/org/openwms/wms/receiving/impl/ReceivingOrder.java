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

import org.ameba.i18n.Translator;
import org.ameba.integration.jpa.ApplicationEntity;
import org.openwms.values.Problem;
import org.openwms.wms.receiving.api.OrderState;
import org.openwms.wms.receiving.api.PositionState;
import org.openwms.wms.receiving.api.events.ReceivingOrderStateChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import static org.openwms.wms.receiving.ReceivingMessages.RO_ALREADY_IN_STATE;
import static org.openwms.wms.receiving.ReceivingMessages.RO_CANCELLATION_DENIED;
import static org.openwms.wms.receiving.TimeProvider.DATE_TIME_WITH_TIMEZONE;
import static org.openwms.wms.receiving.api.OrderState.CANCELED;
import static org.openwms.wms.receiving.api.OrderState.COMPLETED;
import static org.openwms.wms.receiving.api.OrderState.CREATED;
import static org.openwms.wms.receiving.api.OrderState.PARTIALLY_COMPLETED;
import static org.openwms.wms.receiving.api.OrderState.PROCESSING;
import static org.openwms.wms.receiving.api.OrderState.UNDEFINED;
import static org.openwms.wms.receiving.api.OrderState.VALIDATED;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivingOrder.class);

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
    @Column(name = "C_LATEST_DUE_AT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime latestDueDate;

    /** When the order is expected to be received. */
    @Column(name = "C_EXPECTED_RECEIPT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime expectedReceiptDate;

    /** Earliest date the order can be started. */
    @Column(name = "C_START_EARLIEST_AT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime earliestStartDate;

    /** When the order has been started. */
    @Column(name = "C_STARTED_AT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime startDate;

    /** When the order has been finished. */
    @Column(name = "C_ENDED_AT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime endDate;

    /** Date when the order should be allocated. */
    @Column(name = "C_NEXT_ALLOC_AT", columnDefinition = "timestamp(0)")
    @DateTimeFormat(pattern = DATE_TIME_WITH_TIMEZONE)
    private ZonedDateTime nextAllocationDate;

    /** Latest problem that is occurred on this order. */
    @Embedded
    private Problem problem;

    /** All ReceivingOrderPosition this order has. */
    @OneToMany(mappedBy = "order", cascade = {ALL}, fetch = FetchType.EAGER)
    @OrderBy("posNo")
    @Valid
    private List<AbstractReceivingOrderPosition> positions = new ArrayList<>();

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
        this.orderState = CREATED;
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

    protected void setOrderState(ApplicationEventPublisher publisher, OrderState orderState) {
        publisher.publishEvent(new ReceivingOrderStateChangeEvent(this, orderState));
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("ReceivingOrder changed state from [{}] -> [{}]", this.orderState, orderState);
        }
        this.orderState = orderState;
    }

    public void cancelOrder(ApplicationEventPublisher publisher, Translator translator) {
        if (orderState == CANCELED) {
            throw new AlreadyCancelledException(
                    translator,
                    RO_ALREADY_IN_STATE,
                    new String[]{orderId, orderState.name()},
                    orderId, orderState
            );
        }
        if (orderState != UNDEFINED && orderState != CREATED && orderState != VALIDATED) {
            throw new CancellationDeniedException(
                    translator,
                    RO_CANCELLATION_DENIED,
                    new String[]{orderId, orderState.name()},
                    orderId, orderState
            );
        }
        if (positions.isEmpty()) {
            setOrderState(publisher, CANCELED);
        } else {
            // Changing the orderState to CANCELED is done implicitly by changing the positions state
            positions.forEach(p -> p.changePositionState(publisher, PositionState.CANCELED));
        }
    }

    /**
     * Recalculate and occasionally change the state of this {@link ReceivingOrder}.
     *
     * @return {@code true} if the state has been changed
     */
    public boolean recalculateOrderState(ApplicationEventPublisher publisher) {
        var currentPositionStates = positions.stream().map(AbstractReceivingOrderPosition::getState).toList();

        switch (orderState) {
            case CREATED, VALIDATED -> {
                if (currentPositionStates.contains(PositionState.PROCESSING)) {
                    LOGGER.info("At least one ReceivingOrderPosition is in PROCESSING");
                    setOrderState(publisher, PROCESSING);
                    return true;
                } else if (positions.stream().allMatch(p -> p.getState() == PositionState.COMPLETED)) {
                    LOGGER.info("All ReceivingOrderPositions are COMPLETED");
                    setOrderState(publisher, COMPLETED);
                    return true;
                }
            }
            case PROCESSING -> {
                if (!currentPositionStates.contains(PositionState.CREATED) &&
                        !currentPositionStates.contains(PositionState.PROCESSING)) {

                    // Not active anymore: Action required...
                    if (currentPositionStates.contains(PositionState.PARTIALLY_COMPLETED)) {
                        LOGGER.info("All ReceivingOrderPositions are DONE but some are PARTIALLY_COMPLETED");
                        setOrderState(publisher, PARTIALLY_COMPLETED);
                        return true;
                    } else if (currentPositionStates.contains(PositionState.COMPLETED)) {
                        LOGGER.info("All ReceivingOrderPositions are DONE but some are COMPLETED");
                        setOrderState(publisher, COMPLETED);
                        return true;
                    } else {
                        LOGGER.info("All ReceivingOrderPositions are CANCELED");
                        setOrderState(publisher, CANCELED);
                        return true;
                    }
                }
            }
            default -> LOGGER.debug("No state change required, position states are [{}]", currentPositionStates);
        }
        return false;
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

    public void setExpectedReceiptDate(ZonedDateTime expectedReceiptDate) {
        this.expectedReceiptDate = expectedReceiptDate;
    }

    public ZonedDateTime getEarliestStartDate() {
        return earliestStartDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
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

    public List<AbstractReceivingOrderPosition> getPositions() {
        return positions == null ? Collections.emptyList() : positions;
    }

    public void setPositions(List<AbstractReceivingOrderPosition> positions) {
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