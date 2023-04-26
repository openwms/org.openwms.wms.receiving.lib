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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A ReceivingOrderVO.
 *
 * @author Heiko Scherrer
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReceivingOrderVO extends AbstractBase<ReceivingOrderVO> implements Serializable {

    public static final String MEDIA_TYPE = "application/vnd.openwms.receiving-order-v1+json";

    /** The persistent identifier. */
    @JsonProperty("pKey")
    private String pKey;
    /** The unique identifier of this {@code ReceivingOrder}. */
    @JsonProperty("orderId")
    private String orderId;
    /** The current state of this {@code ReceivingOrder}. */
    @JsonProperty("state")
    private String state;
    /** When the order is expected to be received. */
    @JsonProperty("expectedReceiptDate")
    private ZonedDateTime expectedReceiptDate;
    /** A set of {@code ReceivingOrderPosition}s belonging to this {@code ReceivingOrder}. */
    @JsonProperty("positions")
    @JsonManagedReference
    @Valid
    private List<BaseReceivingOrderPositionVO> positions = new ArrayList<>(0);
    /** Arbitrary detail information stored along an order. */
    @JsonProperty("details")
    private Map<String, String> details = new HashMap<>();

    @JsonCreator
    ReceivingOrderVO() {}

    public void sortPositions() {
        if (this.getPositions() != null) {
            this.setPositions(this.getPositions().stream()
                    .sorted(Comparator.comparingInt(BaseReceivingOrderPositionVO::getPositionId))
                    .toList());
        }
    }

    public ReceivingOrderVO(@NotBlank String orderId) {
        this.orderId = orderId;
    }

    public String getpKey() {
        return pKey;
    }

    public void setpKey(String pKey) {
        this.pKey = pKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean hasState() {
        return state != null;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ZonedDateTime getExpectedReceiptDate() {
        return expectedReceiptDate;
    }

    public void setExpectedReceiptDate(ZonedDateTime expectedReceiptDate) {
        this.expectedReceiptDate = expectedReceiptDate;
    }

    public List<BaseReceivingOrderPositionVO> getPositions() {
        return positions;
    }

    public void setPositions(List<BaseReceivingOrderPositionVO> positions) {
        this.positions = positions;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReceivingOrderVO that = (ReceivingOrderVO) o;
        return Objects.equals(pKey, that.pKey) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(state, that.state) &&
                Objects.equals(expectedReceiptDate, that.expectedReceiptDate) &&
                Objects.equals(positions, that.positions) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, orderId, state, expectedReceiptDate, positions, details);
    }

    @Override
    public String toString() {
        return orderId;
    }
}
