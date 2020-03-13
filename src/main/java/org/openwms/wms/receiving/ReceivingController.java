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

import org.ameba.mapping.BeanMapper;
import org.openwms.core.http.AbstractWebController;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * A ReceivingController.
 *
 * @author Heiko Scherrer
 */
@RestController
public class ReceivingController extends AbstractWebController {

    private final ReceivingService service;
    private final BeanMapper mapper;

    public ReceivingController(ReceivingService service, BeanMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping("/v1/receiving")
    public ResponseEntity<Void> createOrder(@RequestBody ReceivingOrderVO order, HttpServletRequest req) {
        try {
            ReceivingOrder saved = service.createOrder(mapper.map(order, ReceivingOrder.class));
            return ResponseEntity.created(getLocationURIForCreatedResource(req, saved.getPersistentKey())).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
