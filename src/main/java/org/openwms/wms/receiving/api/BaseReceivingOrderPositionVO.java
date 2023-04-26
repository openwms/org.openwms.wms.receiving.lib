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
package org.openwms.wms.receiving.api;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.validation.constraints.NotNull;
import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A BaseReceivingOrderPositionVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class BaseReceivingOrderPositionVO implements Serializable {

    @JsonBackReference
    private ReceivingOrderVO order;
    /** The unique identifier of this {@code ReceivingOrder}. */
    @NotNull
    @JsonProperty("orderId")
    private String orderId;
    /** The unique position ID within an ReceivingOrder - must not be empty. */
    @NotNull
    @JsonProperty("positionId")
    private Integer positionId;
    /** Current position state. */
    @JsonProperty("state")
    private String state;
    /** The current priority of the ReceivingOrder the position belongs to. */
    @JsonProperty("priority")
    private int priority;
    /** Optional: How the position should be processed, manually oder automatically. */
    @JsonProperty("startMode")
    private String startMode;
    /** Arbitrary detail information on this position, might be populated with ERP information. */
    @JsonProperty("details")
    private Map<String, String> details;
    /** Timestamp when the position has been created. */
    @JsonProperty("createDt")
    private Date createDt;

    @JsonCreator
    BaseReceivingOrderPositionVO() {}

    @ConstructorProperties("positionId")
    public BaseReceivingOrderPositionVO(@NotNull Integer positionId) {
        this.positionId = positionId;
    }

    public ReceivingOrderVO getOrder() {
        return order;
    }

    public void setOrder(ReceivingOrderVO order) {
        this.order = order;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getStartMode() {
        return startMode;
    }

    public void setStartMode(String startMode) {
        this.startMode = startMode;
    }

    public Map<String, String> getDetails() {
        if (details == null) {
            details = new HashMap<>();
        }
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    @Override
    public String toString() {
        return String.valueOf(positionId);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseReceivingOrderPositionVO)) return false;
        BaseReceivingOrderPositionVO that = (BaseReceivingOrderPositionVO) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(positionId, that.positionId) && Objects.equals(state, that.state) && Objects.equals(priority, that.priority) && Objects.equals(startMode, that.startMode) && Objects.equals(details, that.details) && Objects.equals(createDt, that.createDt);
    }

    /**
     * {@inheritDoc}
     *
     * All fields.
     */
    @Override
    public int hashCode() {
        return Objects.hash(orderId, positionId, state, priority, startMode, details, createDt);
    }
}
