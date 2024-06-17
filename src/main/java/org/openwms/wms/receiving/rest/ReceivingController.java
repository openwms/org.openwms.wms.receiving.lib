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
package org.openwms.wms.receiving.rest;

import jakarta.validation.Valid;
import org.ameba.http.MeasuredRestController;
import org.openwms.core.http.AbstractWebController;
import org.openwms.core.http.Index;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.OrderState;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.openwms.wms.receiving.api.ReceivingOrderVO.MEDIA_TYPE;
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

    private final RestServiceFacadeImpl service;

    ReceivingController(RestServiceFacadeImpl service) {
        this.service = service;
    }

    @GetMapping("/v1/receiving-orders/index")
    public ResponseEntity<Index> index() {
        return ResponseEntity.ok(
                new Index(
                        linkTo(methodOn(ReceivingOrderFinder.class).findAll()).withRel("receiving-order-findall"),
                        linkTo(methodOn(ReceivingOrderFinder.class).findOrder("b65a7658-c53c-4a81-8abb-75ab67783f47")).withRel("receiving-order-findbypkey"),
                        linkTo(methodOn(ReceivingOrderFinder.class).findOrderByOrderId("4711")).withRel("receiving-order-findbyorderid"),
                        linkTo(methodOn(ReceivingOrderCreator.class).createOrder(new ReceivingOrderVO("4711"), null)).withRel("receiving-order-create"),
                        linkTo(methodOn(ReceivingController.class).captureOrder("b65a7658-c53c-4a81-8abb-75ab67783f48", asList(new CaptureRequestVO()))).withRel("receiving-order-capture"),
                        linkTo(methodOn(ReceivingController.class).captureBlindReceipt(asList(new CaptureRequestVO()))).withRel("receiving-order-blind-receipt"),
                        linkTo(methodOn(ReceivingController.class).completeOrder("b65a7658-c53c-4a81-8abb-75ab67783f49")).withRel("receiving-order-complete"),
                        linkTo(methodOn(ReceivingController.class).saveOrder("b65a7658-c53c-4a81-8abb-75ab67783f46", null)).withRel("receiving-order-save"),
                        linkTo(methodOn(ReceivingController.class).patchOrder("b65a7658-c53c-4a81-8abb-75ab67783f45", null)).withRel("receiving-order-patch")
                )
        );
    }

    @PostMapping(value = "/v1/receiving-orders/{pKey}/capture", produces = MEDIA_TYPE)
    public ResponseEntity<ReceivingOrderVO> captureOrder(
            @PathVariable("pKey") String pKey,
            @Valid @RequestBody List<CaptureRequestVO> requests) {

        Optional<ReceivingOrderVO> result = service.capture(pKey, requests);
        if (result.isPresent()) {
            result.get().sortPositions();
            return ResponseEntity.ok(result.get());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/v1/capture", produces = MEDIA_TYPE)
    public ResponseEntity<Void> captureBlindReceipt(@RequestBody List<CaptureRequestVO> requests) {

        service.captureBlindReceipts(requests);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/v1/receiving-orders/{pKey}/complete", produces = MEDIA_TYPE)
    public ResponseEntity<ReceivingOrderVO> completeOrder(
            @PathVariable("pKey") String pKey) {

        var result = service.complete(pKey);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/v1/receiving-orders/{pKey}", produces = MEDIA_TYPE, consumes = MEDIA_TYPE)
    public ResponseEntity<ReceivingOrderVO> saveOrder(
            @PathVariable("pKey") String pKey,
            @Valid @RequestBody ReceivingOrderVO receivingOrder){
        return ResponseEntity.ok(service.update(pKey, receivingOrder));
    }

    @PatchMapping(value = "/v1/receiving-orders/{pKey}", produces = MEDIA_TYPE, consumes = MEDIA_TYPE)
    public ResponseEntity<ReceivingOrderVO> patchOrder(
            @PathVariable("pKey") String pKey,
            @Valid @RequestBody ReceivingOrderVO receivingOrder){

        if (receivingOrder.hasState()) {
            var state = OrderState.valueOf(receivingOrder.getState());
            if (state == OrderState.CANCELED) {
                return ResponseEntity.ok(service.cancelOrder(pKey));
            }
            return ResponseEntity.ok(service.changeState(pKey, state));
        }
        receivingOrder.sortPositions();
        return ResponseEntity.ok(receivingOrder);
    }
}
