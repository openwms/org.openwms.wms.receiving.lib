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
package org.openwms.wms.receiving;

import org.ameba.exception.NotFoundException;
import org.ameba.http.MeasuredRestController;
import org.ameba.mapping.BeanMapper;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.openwms.core.units.api.Piece;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingService;
import org.openwms.wms.receiving.inventory.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static java.lang.String.format;
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
                        linkTo(methodOn(ReceivingController.class).captureOrder("b65a7658-c53c-4a81-8abb-75ab67783f47", "EURO", new CaptureRequestVO())).withRel("receiving-order-capture"),
                        linkTo(methodOn(ReceivingController.class).cancelOrder("b65a7658-c53c-4a81-8abb-75ab67783f47")).withRel("receiving-order-cancel")
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
    public ResponseEntity<Void> createOrder(@Valid @RequestBody ReceivingOrderVO orderVO, HttpServletRequest req) {
        // FIXME [openwms]:
        orderVO.getPositions().clear();
        ReceivingOrder order = mapper.map(orderVO, ReceivingOrder.class);
        // FIXME [openwms]:
        order.getPositions().forEach(p -> {
            p.setQuantityReceived(Piece.ZERO);
            p.setQuantityExpected(Piece.of(1));
        });
        ReceivingOrder saved = service.createOrder(order);
        return ResponseEntity.created(getLocationURIForCreatedResource(req, saved.getPersistentKey())).build();
    }

    @PostMapping(value = "/v1/receiving-orders/{pKey}/capture", params = "loadUnitType")
    public ResponseEntity<Void> captureOrder(
            @PathVariable("pKey") String pKey,
            @RequestParam("loadUnitType") String loadUnitType,
            @Valid @RequestBody CaptureRequestVO request) {
        Product product = mapper.map(request.getProduct(), Product.class);
        service.capture(
                pKey,
                request.getTransportUnitId(),
                request.getLoadUnitLabel(),
                loadUnitType,
                request.getQuantityReceived(),
                request.getDetails(),
                product);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/receiving-orders/{pKey}")
    public ResponseEntity<Void> cancelOrder(@PathVariable("pKey") String pKey){
        service.cancelOrder(pKey);
        return ResponseEntity.noContent().build();
    }
}
