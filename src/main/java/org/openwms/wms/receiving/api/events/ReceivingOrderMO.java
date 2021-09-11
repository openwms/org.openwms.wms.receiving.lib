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
package org.openwms.wms.receiving.api.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ameba.http.AbstractBase;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A ReceivingOrderMO.
 *
 * @author Heiko Scherrer
 */
public class ReceivingOrderMO implements Serializable {

    /** The persistent identifier. */
    private String pKey;
    /** The unique identifier of this {@code ReceivingOrder}. */
    private String orderId;
    /** The current state of this {@code ReceivingOrder. */
    private String state;
    /** A set of {@code ReceivingOrderPosition}s belonging to this {@code ReceivingOrder. */
    private List<@Valid ReceivingOrderPositionMO> positions = new ArrayList<>(0);
    /** Arbitrary detail information stored along an order. */
    private Map<String, String> details = new HashMap<>();

    public String getpKey() {
        return pKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

     public List<ReceivingOrderPositionMO> getPositions() {
        return positions;
    }

    public void setPositions(List<ReceivingOrderPositionMO> positions) {
        this.positions = positions;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public String getState() {
        return state;
    }

    public boolean hasState() {
        return state != null;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ReceivingOrderMO that = (ReceivingOrderMO) o;
        return Objects.equals(pKey, that.pKey) &&
                Objects.equals(orderId, that.orderId) &&
                Objects.equals(state, that.state) &&
                Objects.equals(positions, that.positions) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pKey, orderId, state, positions, details);
    }

    @Override
    public String toString() {
        return orderId;
    }
}
