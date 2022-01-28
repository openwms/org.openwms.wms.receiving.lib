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

import org.openwms.wms.receiving.ValidationGroups;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A ReceivingTransportUnitOrderPosition is a persisted entity class that represents an expected receipt of a
 * {@code TransportUnit}.
 * 
 * @author Heiko Scherrer
 */
@Entity
@Table(name = "WMS_REC_ORDER_POS_TU")
public class ReceivingTransportUnitOrderPosition extends BaseReceivingOrderPosition implements Convertable, Serializable {

    /** The business key of the expected {@code TransportUnit} that is expected to be received. */
    @Column(name = "C_TRANSPORT_UNIT_BK")
    @NotNull(groups = ValidationGroups.CreateExpectedTUReceipt.class)
    private String transportUnitBK;

    /** The name of the {@code TransportUnitType} the expected {@code TransportUnit} is of. */
    @Column(name = "C_TRANSPORT_UNIT_TYPE_NAME")
    @NotNull(groups = ValidationGroups.CreateExpectedTUReceipt.class)
    private String transportUnitTypeName;

    /** Used by the JPA provider. */
    protected ReceivingTransportUnitOrderPosition() {}

    public ReceivingTransportUnitOrderPosition(Integer posNo, String transportUnitBK, String transportUnitTypeName) {
        super(posNo);
        this.transportUnitBK = transportUnitBK;
        this.transportUnitTypeName = transportUnitTypeName;
    }

    @Override
    public void validateOnCreation(Validator validator, Class<?> clazz) {
        if (clazz.isAssignableFrom(ValidationGroups.Create.class)) {
            validator.validate(this, ValidationGroups.CreateExpectedTUReceipt.class);
        }
        validator.validate(this, clazz);
    }

    @Override
    public void accept(BaseReceivingOrderPositionVisitor visitor) {
        visitor.visit(this);
    }

    public String getTransportUnitBK() {
        return transportUnitBK;
    }

    public String getTransportUnitTypeName() {
        return transportUnitTypeName;
    }
}