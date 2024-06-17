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

import jakarta.servlet.http.HttpServletRequest;
import org.ameba.http.MeasuredRestController;
import org.openwms.core.http.AbstractWebController;
import org.openwms.wms.receiving.CycleAvoidingMappingContext;
import org.openwms.wms.receiving.ReceivingMapper;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.ReceivingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.openwms.wms.receiving.api.ReceivingOrderVO.MEDIA_TYPE;

/**
 * A ReceivingOrderCreator.
 *
 * @author Heiko Scherrer
 */
@Validated
@MeasuredRestController
public class ReceivingOrderCreator extends AbstractWebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivingOrderCreator.class);
    private final ReceivingService<?> service;
    private final ReceivingMapper receivingMapper;

    public ReceivingOrderCreator(ReceivingService<?> service, ReceivingMapper receivingMapper) {
        this.service = service;
        this.receivingMapper = receivingMapper;
    }

    @Transactional
    @PostMapping(value = "/v1/receiving-orders", consumes = MEDIA_TYPE, produces = MEDIA_TYPE)
    public ResponseEntity<ReceivingOrderVO> createOrder(
            @RequestBody ReceivingOrderVO orderVO,
            HttpServletRequest req) {

        LOGGER.debug("Requested to create ReceivingOrder with quantities [{}]", orderVO);
        var saved = service.createOrder(receivingMapper.convertVO(orderVO, new CycleAvoidingMappingContext()));
        return ResponseEntity
                .created(getLocationURIForCreatedResource(req, saved.getPersistentKey()))
                .body(receivingMapper.convertToVO(saved, new CycleAvoidingMappingContext()));
    }
}
