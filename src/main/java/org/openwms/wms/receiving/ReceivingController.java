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
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivingController.class);
    private final ReceivingService service;
    private final BeanMapper mapper;
    private final ReceivingMapper receivingMapper;

    ReceivingController(ReceivingService service, BeanMapper mapper, ReceivingMapper receivingMapper) {
        this.service = service;
        this.mapper = mapper;
        this.receivingMapper = receivingMapper;
    }

    @GetMapping("/v1/receiving-orders/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(ReceivingController.class).findAll()).withRel("receiving-order-findall"),
                        linkTo(methodOn(ReceivingController.class).findOrder("b65a7658-c53c-4a81-8abb-75ab67783f47")).withRel("receiving-order-findbypkey"),
                        linkTo(methodOn(ReceivingController.class).findOrderByOrderId("4711")).withRel("receiving-order-findbyorderid"),
                        linkTo(methodOn(ReceivingController.class).createOrder(new ReceivingOrderVO("4711"), null)).withRel("receiving-order-create"),
                        linkTo(methodOn(ReceivingController.class).createExpectedTUReceipt(new ReceivingOrderVO("4711"), null)).withRel("receiving-order-create-tu-receipt"),
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
        List<ReceivingOrder> all = service.findAll();
        var result = receivingMapper.convertToVO(all, new CycleAvoidingMappingContext());
        result.forEach(ReceivingOrderVO::sortPositions);
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @GetMapping("/v1/receiving-orders/{pKey}")
    public ResponseEntity<ReceivingOrderVO> findOrder(@PathVariable("pKey") String pKey) {
        var result = receivingMapper.convertToVO(service.findByPKey(pKey), new CycleAvoidingMappingContext());
        result.sortPositions();
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/v1/receiving-orders", params = {"orderId"})
    public ResponseEntity<ReceivingOrderVO> findOrderByOrderId(@RequestParam("orderId") String orderId) {
        var vo = receivingMapper.convertToVO(
                service.findByOrderId(orderId).orElseThrow(() -> new NotFoundException(format("No ReceivingOrder with orderId [%s] exists", orderId))),
                new CycleAvoidingMappingContext());
        vo.sortPositions();
        return ResponseEntity.ok(vo);
    }

    @Transactional
    @Validated(ValidationGroups.CreateQuantityReceipt.class)
    @PostMapping("/v1/receiving-orders")
    public ResponseEntity<ReceivingOrderVO> createOrder(
            @Validated(ValidationGroups.CreateQuantityReceipt.class)
            @Valid @RequestBody ReceivingOrderVO orderVO,
            HttpServletRequest req) {
        LOGGER.debug("Requested to create ReceivingOrder with quantities [{}]", orderVO);
        ReceivingOrder saved = service.createOrder(receivingMapper.convertVO(orderVO, new CycleAvoidingMappingContext()));
        return ResponseEntity
                .created(getLocationURIForCreatedResource(req, saved.getPersistentKey()))
                .body(receivingMapper.convertToVO(saved, new CycleAvoidingMappingContext()));
    }

    @Transactional
    @Validated(ValidationGroups.CreateExpectedTUReceipt.class)
    @PostMapping(value = "/v1/receiving-orders", consumes = "application/vnd.openwms.receiving-order-v2+json")
    public ResponseEntity<ReceivingOrderVO> createExpectedTUReceipt(
            @Valid @RequestBody ReceivingOrderVO orderVO,
            HttpServletRequest req) {
        LOGGER.debug("Requested to create ReceivingOrder with expected TU [{}]", orderVO);
        var eo = receivingMapper.convertVO(orderVO, new CycleAvoidingMappingContext());
        ReceivingOrder saved = service.createOrder(eo);
        return ResponseEntity
                .created(getLocationURIForCreatedResource(req, saved.getPersistentKey()))
                .body(receivingMapper.convertToVO(saved, new CycleAvoidingMappingContext()));
    }

    @PostMapping(value = "/v1/receiving-orders/{pKey}/capture", params = "loadUnitType")
    public ResponseEntity<ReceivingOrderVO> captureOrder(
            @PathVariable("pKey") String pKey,
            @RequestParam("loadUnitType") String loadUnitType,
            @Valid @RequestBody List<CaptureRequestVO> requests) {
        ReceivingOrderVO result = service.capture(pKey, loadUnitType, requests);
        result.sortPositions();
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/v1/receiving-orders/{pKey}/complete")
    public ResponseEntity<ReceivingOrderVO> completeOrder(
            @PathVariable("pKey") String pKey) {
        ReceivingOrderVO result = service.complete(pKey);
        result.sortPositions();
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/v1/receiving-orders/{pKey}")
    public ResponseEntity<ReceivingOrderVO> saveOrder(
            @PathVariable("pKey") String pKey,
            @Valid @RequestBody ReceivingOrderVO receivingOrder
    ){
        var vo = mapper.map(service.update(pKey, receivingOrder), ReceivingOrderVO.class);
        vo.sortPositions();
        return ResponseEntity.ok(vo);
    }

    @PatchMapping(value = "/v1/receiving-orders/{pKey}")
    public ResponseEntity<ReceivingOrderVO> patchOrder(
            @PathVariable("pKey") String pKey,
            @Valid @NotNull @RequestBody ReceivingOrderVO receivingOrder
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
        var vo = mapper.map(updated, ReceivingOrderVO.class);
        vo.sortPositions();
        return ResponseEntity.ok(vo);
    }
}
