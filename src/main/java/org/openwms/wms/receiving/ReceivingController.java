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
package org.openwms.wms.receiving;

import org.ameba.exception.NotFoundException;
import org.ameba.http.MeasuredRestController;
import org.ameba.mapping.BeanMapper;
import org.ameba.tenancy.TenantHolder;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingOrderPosition;
import org.openwms.wms.receiving.impl.ReceivingService;
import org.openwms.wms.receiving.inventory.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.ameba.Constants.HEADER_VALUE_X_TENANT;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * A ReceivingController.
 *
 * @author Heiko Scherrer
 */
@Validated
@MeasuredRestController
public class ReceivingController extends AbstractWebController {

    private final ReceivingService service;
    private final BeanMapper mapper;

    ReceivingController(ReceivingService service, BeanMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/v1/receiving-orders/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(ReceivingController.class).findAll()).withRel("receiving-order-findall"),
                        linkTo(methodOn(ReceivingController.class).findOrder("b65a7658-c53c-4a81-8abb-75ab67783f47")).withRel("receiving-order-findbypkey"),
                        linkTo(methodOn(ReceivingController.class).findOrderByOrderId("4711")).withRel("receiving-order-findbyorderid"),
                        linkTo(methodOn(ReceivingController.class).createOrder(new ReceivingOrderVO("4711"), null)).withRel("receiving-order-create"),
                        linkTo(methodOn(ReceivingController.class).captureOrder("b65a7658-c53c-4a81-8abb-75ab67783f47", "EURO", asList(new CaptureRequestVO()))).withRel("receiving-order-capture"),
                        linkTo(methodOn(ReceivingController.class).completeOrder("b65a7658-c53c-4a81-8abb-75ab67783f47")).withRel("receiving-order-complete"),
                        linkTo(methodOn(ReceivingController.class).saveOrder("b65a7658-c53c-4a81-8abb-75ab67783f47", null)).withRel("receiving-order-save"),
                        linkTo(methodOn(ReceivingController.class).patchOrder("b65a7658-c53c-4a81-8abb-75ab67783f47", null)).withRel("receiving-order-patch")
                )
        );
    }

    @Transactional(readOnly = true)
    @GetMapping("/v1/receiving-orders")
    public ResponseEntity<List<ReceivingOrderVO>> findAll() {
        List<ReceivingOrder> orders = service.findAll();
        List<ReceivingOrderVO> result = mapper.map(orders, ReceivingOrderVO.class);
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @GetMapping("/v1/receiving-orders/{pKey}")
    public ResponseEntity<ReceivingOrderVO> findOrder(@PathVariable("pKey") String pKey) {
        ReceivingOrder order = service.findByPKey(pKey);
        return ResponseEntity.ok(mapper.map(order, ReceivingOrderVO.class));
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/v1/receiving-orders", params = {"orderId"})
    public ResponseEntity<ReceivingOrderVO> findOrderByOrderId(@RequestParam("orderId") String orderId) {
        ReceivingOrder order = service.findByOrderId(orderId).orElseThrow(() -> new NotFoundException(format("No ReceivingOrder with orderId [%s] exists", orderId)));
        return ResponseEntity.ok(mapper.map(order, ReceivingOrderVO.class));
    }

    @PostMapping("/v1/receiving-orders")
    public ResponseEntity<Void> createOrder(
            @Valid @RequestBody ReceivingOrderVO orderVO,
            HttpServletRequest req) {
        ReceivingOrder order = mapper.map(orderVO, ReceivingOrder.class);
        order.getPositions().clear();
        order.getPositions().addAll(orderVO.getPositions().stream().map(p -> {
            ReceivingOrderPosition rop = mapper.map(p, ReceivingOrderPosition.class);
            rop.setOrder(order);
            rop.setProduct(mapper.map(p.getProduct(), Product.class));
            return rop;
        }).collect(Collectors.toList()));
        ReceivingOrder saved = service.createOrder(order);
        return ResponseEntity.created(getLocationURIForCreatedResource(req, saved.getPersistentKey())).build();
    }

    @PostMapping(value = "/v1/receiving-orders/{pKey}/capture", params = "loadUnitType")
    public ResponseEntity<ReceivingOrderVO> captureOrder(
            @PathVariable("pKey") String pKey,
            @RequestParam("loadUnitType") String loadUnitType,
            @Valid @RequestBody List<CaptureRequestVO> requests) {
        ReceivingOrderVO result = service.capture(pKey, loadUnitType, requests);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/v1/receiving-orders/{pKey}/complete")
    public ResponseEntity<ReceivingOrderVO> completeOrder(
            @PathVariable("pKey") String pKey) {
        ReceivingOrderVO result = service.complete(pKey);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/v1/receiving-orders/{pKey}")
    public ResponseEntity<ReceivingOrderVO> saveOrder(
            @PathVariable("pKey") String pKey,
            @Valid @RequestBody ReceivingOrderVO receivingOrder
    ){
        return ResponseEntity.ok(mapper.map(service.update(pKey, receivingOrder), ReceivingOrderVO.class));
    }

    @PatchMapping(value = "/v1/receiving-orders/{pKey}")
    public ResponseEntity<ReceivingOrderVO> patchOrder(
            @PathVariable("pKey") String pKey,
            @Valid @RequestBody ReceivingOrderVO receivingOrder
    ){
        ReceivingOrder updated = mapper.map(receivingOrder, ReceivingOrder.class);
        if (receivingOrder.hasState()) {
            OrderState state = OrderState.valueOf(receivingOrder.getState());
            if (state == OrderState.CANCELED) {
                updated = service.cancelOrder(pKey);
            } else {
                updated = service.changeState(pKey, state);
            }
        }
        return ResponseEntity.ok(mapper.map(updated, ReceivingOrderVO.class));
    }
}
