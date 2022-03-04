/*
 * Copyright 2005-2022 the original author or authors.
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

import org.ameba.exception.NotFoundException;
import org.ameba.http.MeasuredRestController;
import org.ameba.i18n.Translator;
import org.openwms.core.http.AbstractWebController;
import org.openwms.wms.receiving.CycleAvoidingMappingContext;
import org.openwms.wms.receiving.ReceivingMapper;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.ReceivingService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.openwms.wms.receiving.ReceivingMessages.RO_NOT_FOUND_BY_BK;
import static org.openwms.wms.receiving.api.ReceivingOrderVO.MEDIA_TYPE;

/**
 * A ReceivingOrderFinder.
 *
 * @author Heiko Scherrer
 */
@Validated
@MeasuredRestController
public class ReceivingOrderFinder extends AbstractWebController {

    private final ReceivingService<CaptureRequestVO> service;
    private final Translator translator;
    private final ReceivingMapper receivingMapper;

    public ReceivingOrderFinder(ReceivingService<CaptureRequestVO> service, Translator translator, ReceivingMapper receivingMapper) {
        this.service = service;
        this.translator = translator;
        this.receivingMapper = receivingMapper;
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/v1/receiving-orders", produces = MEDIA_TYPE)
    public ResponseEntity<List<ReceivingOrderVO>> findAll() {

        var result = receivingMapper.convertToVO(service.findAll(), new CycleAvoidingMappingContext());
        result.forEach(ReceivingOrderVO::sortPositions);
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/v1/receiving-orders/{pKey}", produces = MEDIA_TYPE)
    public ResponseEntity<ReceivingOrderVO> findOrder(
            @PathVariable("pKey") String pKey) {

        var result = receivingMapper.convertToVO(service.findByPKey(pKey), new CycleAvoidingMappingContext());
        result.sortPositions();
        return ResponseEntity.ok(result);
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/v1/receiving-orders", params = {"orderId"}, produces = MEDIA_TYPE)
    public ResponseEntity<ReceivingOrderVO> findOrderByOrderId(
            @RequestParam("orderId") String orderId) {

        var vo = receivingMapper.convertToVO(
                service.findByOrderId(orderId).orElseThrow(
                        () -> new NotFoundException(translator, RO_NOT_FOUND_BY_BK, new String[]{orderId}, orderId)),
                new CycleAvoidingMappingContext()
        );
        vo.sortPositions();
        return ResponseEntity.ok(vo);
    }
}
