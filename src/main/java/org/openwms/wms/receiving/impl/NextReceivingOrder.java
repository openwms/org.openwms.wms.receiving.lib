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

import org.ameba.integration.jpa.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Objects;

/**
 * A NextReceivingOrder.
 *
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_ORDER_ID", uniqueConstraints = @UniqueConstraint(columnNames = {"C_NAME"}))
public class NextReceivingOrder extends BaseEntity implements Serializable {

    /** Name of the Account. */
    @Column(name = "C_NAME")
    private String name;
    /** Last given OrderId. */
    @Column(name = "C_CURRENT", length = 40)
    private String currentOrderId;

    /** Dear JPA... */
    public NextReceivingOrder() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(String currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NextReceivingOrder)) return false;
        NextReceivingOrder that = (NextReceivingOrder) o;
        return Objects.equals(name, that.name) && Objects.equals(currentOrderId, that.currentOrderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, currentOrderId);
    }

    @Override
    public String toString() {
        return currentOrderId;
    }
}
