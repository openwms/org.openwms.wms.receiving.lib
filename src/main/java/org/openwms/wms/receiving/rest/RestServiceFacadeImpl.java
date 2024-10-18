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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.ameba.annotation.Measured;
import org.openwms.wms.receiving.CycleAvoidingMappingContext;
import org.openwms.wms.receiving.ReceivingMapper;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.OrderState;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.impl.ReceivingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * A RestServiceFacadeImpl.
 *
 * @author Heiko Scherrer
 */
@Service
public class RestServiceFacadeImpl<T extends CaptureRequestVO> implements RestServiceFacade<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceFacadeImpl.class);
    private final ReceivingMapper receivingMapper;
    private final ReceivingService receivingService;

    public RestServiceFacadeImpl(ReceivingMapper receivingMapper, ReceivingService receivingService) {
        this.receivingMapper = receivingMapper;
        this.receivingService = receivingService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional
    public @NotNull Optional<ReceivingOrderVO> capture(
            @NotBlank String pKey,
            @NotNull @Valid List<T> requests
    ) {
        Optional<ReceivingOrder> optOrder = receivingService.capture(pKey, requests);
        if (optOrder.isPresent()) {
            var eo = receivingService.findByPKey(optOrder.get().getPersistentKey());
            var vo = receivingMapper.convertToVO(eo, new CycleAvoidingMappingContext());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Captured ReceivingOrder [{}], new instance is [{}]", pKey, vo);
            }
            return Optional.of(vo);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void captureBlindReceipts(
            @NotNull List<T> requests
    ) {
        receivingService.captureBlindReceipts(requests);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional
    public @NotNull ReceivingOrderVO cancelOrder(@NotBlank String pKey) {
        receivingService.cancelOrder(pKey);
        var eo = receivingService.findByPKey(pKey);
        var result = receivingMapper.convertToVO(eo, new CycleAvoidingMappingContext());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Returning ReceivingOrder after cancellation [{}]", result);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional
    public @NotNull ReceivingOrderVO changeState(@NotBlank String pKey, @NotNull OrderState state) {
        receivingService.changeState(pKey, state);
        var vo = receivingMapper.convertToVO(receivingService.findByPKey(pKey), new CycleAvoidingMappingContext());
        vo.sortPositions();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Changed state of ReceivingOrder [{}], new instance is [{}]", pKey, vo);
        }
        return vo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional
    public @NotNull ReceivingOrderVO update(@NotBlank String pKey, @NotNull ReceivingOrderVO receivingOrder) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating the ReceivingOrder with pKey [{}] with content [{}]", pKey, receivingOrder);
        }
        var eo = receivingMapper.convertVO(receivingOrder, new CycleAvoidingMappingContext());
        var updated = receivingService.update(pKey, eo);
        var vo = receivingMapper.convertToVO(updated, new CycleAvoidingMappingContext());
        vo.sortPositions();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updated ReceivingOrder [{}], new instance is [{}]", pKey, vo);
        }
        return vo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional
    public @NotNull ReceivingOrderVO complete(@NotBlank String pKey) {
        var eo = receivingService.complete(pKey);
        eo = receivingService.findByPKey(eo.getPersistentKey());
        var vo = receivingMapper.convertToVO(eo, new CycleAvoidingMappingContext());
        vo.sortPositions();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Completed whole ReceivingOrder with pKey [{}], updated instance is [{}]", pKey, vo);
        }
        return vo;
    }
}
