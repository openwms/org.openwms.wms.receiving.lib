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

import org.ameba.system.ValidationUtil;
import org.openwms.wms.receiving.ValidationGroups;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// Bug in hibernate prevents overrding fkcontraints
@AssociationOverride(name = "order", joinColumns =
    @JoinColumn(name = "C_ORDER_ID", referencedColumnName = "C_ORDER_ID"),
        foreignKey = @ForeignKey(name = "FK_REC_POS_ORDER_ID_TU"))
@Table(name = "WMS_REC_ORDER_POS_TU",
        uniqueConstraints = @UniqueConstraint(name = "UC_ORDER_ID_POS_TU", columnNames = { "C_ORDER_ID", "C_POS_NO" }))
public class ReceivingTransportUnitOrderPosition extends AbstractReceivingOrderPosition implements Convertable, Serializable {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateOnCreation(Validator validator, Class<?> clazz) {
        if (clazz.isAssignableFrom(ValidationGroups.Create.class)) {
            ValidationUtil.validate(validator,this, ValidationGroups.CreateExpectedTUReceipt.class);
        }
        validator.validate(this, clazz);
    }

    /**
     * {@inheritDoc}
     */
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