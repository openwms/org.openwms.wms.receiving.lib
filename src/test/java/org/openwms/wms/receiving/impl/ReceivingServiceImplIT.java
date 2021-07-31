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

import org.ameba.exception.ResourceExistsException;
import org.ameba.exception.ServiceLayerException;
import org.junit.jupiter.api.Test;
import org.openwms.core.units.api.Piece;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.ReceivingApplicationTest;
import org.openwms.wms.receiving.inventory.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openwms.wms.receiving.TestData.PRODUCT1_SKU;

/**
 * A ReceivingServiceImplTest.
 *
 * @author Heiko Scherrer
 */
@ReceivingApplicationTest
@Transactional
@Rollback
class ReceivingServiceImplIT {

    @Autowired
    private ReceivingServiceImpl service;

    @Test void createOrderWithNull() {
        ServiceLayerException ex = assertThrows(ServiceLayerException.class, () -> service.createOrder(null));
        assertThat(ex.getMessage()).containsIgnoringCase("order");
    }

    @Test void createOrder() {
        ReceivingOrder order = service.createOrder(new ReceivingOrder("4710"));
        assertThat(order.isNew()).isFalse();

        ResourceExistsException ex = assertThrows(ResourceExistsException.class, () -> service.createOrder(new ReceivingOrder("4710")));
        assertThat(ex.getMessage()).containsIgnoringCase("exists");
    }

    @Test void createOrderWithoutID() {
        ReceivingOrder order = service.createOrder(new ReceivingOrder());
        assertThat(order.isNew()).isFalse();
        assertThat(order.getOrderId()).isNotNull();

        ReceivingOrder order2 = service.createOrder(new ReceivingOrder());
        assertThat(order2.getOrderId()).isNotNull();
        assertThat(Integer.parseInt(order.getOrderId()) + 1).isEqualTo(Integer.parseInt(order2.getOrderId()));
    }

    @Test void createOrderFull() {
        ReceivingOrder ro = new ReceivingOrder("4710");
        ro.setDetails(Map.of("p1", "v1", "p2", "v2", "p3", "v3"));
        ReceivingOrderPosition rop = new ReceivingOrderPosition(1, Piece.of(2), new Product(PRODUCT1_SKU));
        rop.setQuantityReceived(Piece.of(1));
        rop.addDetail("p1", "v1").addDetail("p2", "v2");
        ro.getPositions().add(rop);
        ReceivingOrder order = service.createOrder(ro);

        assertThat(order.isNew()).isFalse();
        assertThat(order.getOrderState()).isEqualTo(OrderState.CREATED);
        assertThat(order.getDetails()).hasSize(3);
        assertThat(order.getDetails()).hasSize(3);
        ReceivingOrderPosition next = order.getPositions().iterator().next();
        assertThat(next.getQuantityExpected()).isEqualTo(Piece.of(2));
        assertThat(next.getQuantityReceived()).isEqualTo(Piece.of(1));
        assertThat(next.getDetails()).hasSize(2);
    }
}