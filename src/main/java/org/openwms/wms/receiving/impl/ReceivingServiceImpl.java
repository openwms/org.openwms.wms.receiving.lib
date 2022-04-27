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
package org.openwms.wms.receiving.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.tenancy.TenantHolder;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.CycleAvoidingMappingContext;
import org.openwms.wms.receiving.ReceivingMapper;
import org.openwms.wms.receiving.ServiceProvider;
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static org.openwms.wms.ReceivingConstants.DEFAULT_ACCOUNT_NAME;
import static org.openwms.wms.order.OrderState.CANCELED;
import static org.openwms.wms.order.OrderState.COMPLETED;
import static org.openwms.wms.order.OrderState.CREATED;
import static org.openwms.wms.order.OrderState.UNDEFINED;
import static org.openwms.wms.receiving.ReceivingMessages.RO_ALREADY_EXISTS;
import static org.openwms.wms.receiving.ReceivingMessages.RO_ALREADY_IN_STATE;
import static org.openwms.wms.receiving.ReceivingMessages.RO_CANCELLATION_DENIED;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NOT_FOUND_BY_PKEY;

/**
 * A ReceivingServiceImpl is a Spring managed transactional Services that deals with {@link ReceivingOrder}s.
 * 
 * @author Heiko Scherrer
 */
@Validated
@TxService
class ReceivingServiceImpl<T extends CaptureRequestVO> implements ReceivingService<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivingServiceImpl.class);
    private final Validator validator;
    private final ReceivingMapper receivingMapper;
    private final NextReceivingOrderRepository nextReceivingOrderRepository;
    private final ReceivingOrderRepository repository;
    private final PluginRegistry<ReceivingOrderUpdater, ReceivingOrderUpdater.Type> plugins;
    private final PluginRegistry<ReceivingOrderCapturer<T>, CaptureRequestVO> capturers;
    private final ApplicationEventPublisher publisher;
    private final ServiceProvider serviceProvider;

    ReceivingServiceImpl(
            Validator validator, ReceivingMapper receivingMapper, NextReceivingOrderRepository nextReceivingOrderRepository,
            ReceivingOrderRepository repository,
            @Qualifier("plugins") PluginRegistry<ReceivingOrderUpdater, ReceivingOrderUpdater.Type> plugins,
            @Qualifier("capturers") PluginRegistry<ReceivingOrderCapturer<T>, CaptureRequestVO> capturers,
            ApplicationEventPublisher publisher, ServiceProvider serviceProvider) {
        this.validator = validator;
        this.receivingMapper = receivingMapper;
        this.nextReceivingOrderRepository = nextReceivingOrderRepository;
        this.repository = repository;
        this.plugins = plugins;
        this.capturers = capturers;
        this.publisher = publisher;
        this.serviceProvider = serviceProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull ReceivingOrder createOrder(@NotNull ReceivingOrder order) {
        Optional<ReceivingOrder> opt;
        if (order.hasOrderId()) {
            opt = repository.findByOrderId(order.getOrderId());
            if (opt.isPresent()) {
                throw new ResourceExistsException(serviceProvider.getTranslator(), RO_ALREADY_EXISTS, new String[]{order.getOrderId()}, order.getOrderId());
            }
        } else {
            assignOrderId(order);
        }
        order.getPositions().stream()
                .filter(ReceivingOrderPosition.class::isInstance)
                .forEach(p -> {
                    p.preCreate(serviceProvider);
                    p.validateOnCreation(validator, ValidationGroups.Create.class);
                });
        order = repository.save(order);
        publisher.publishEvent(new ReceivingOrderCreatedEvent(order));
        return order;
    }

    private void assignOrderId(ReceivingOrder order) {
        String currentTenant = TenantHolder.getCurrentTenant() == null ? DEFAULT_ACCOUNT_NAME : TenantHolder.getCurrentTenant();
        Optional<NextReceivingOrder> byName = nextReceivingOrderRepository.findByName(currentTenant);
        NextReceivingOrder nb;
        if (byName.isEmpty()) {
            nb = new NextReceivingOrder();
            nb.setName(currentTenant);
            nb.setCurrentOrderId("1");
        } else {
            nb = byName.get();
            int current = Integer.parseInt(nb.getCurrentOrderId());
            nb.setCurrentOrderId(String.valueOf(++current));
        }
        nextReceivingOrderRepository.save(nb);
        order.setOrderId(nb.getCompleteOrderId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull ReceivingOrderVO capture(@NotBlank String pKey, @NotNull @Valid List<T> requests) {
        ReceivingOrder ro = null;
        for (T request : requests) {
            ro = capturers.getPluginFor(request)
                    .orElseThrow(() -> new IllegalArgumentException("Type of CaptureRequestVO not supported"))
                    .capture(pKey, request);
        }
        return receivingMapper.convertToVO(repository.save(ro), new CycleAvoidingMappingContext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull ReceivingOrder findByPKey(@NotBlank String pKey) {
        return getOrder(pKey);
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public Optional<ReceivingOrder> findByOrderId(@NotBlank String orderId) {
        return repository.findByOrderId(orderId);
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public @NotNull ReceivingOrder update(@NotBlank String pKey, @NotNull ReceivingOrder receivingOrder) {
        var order = getOrder(pKey);
        LOGGER.info("Updating ReceivingOrder [{}]", order.getOrderId());
        var plugins = this.plugins.getPlugins();
        for (ReceivingOrderUpdater plugin : plugins) {
            order = plugin.update(order, receivingOrder);
        }
        return repository.save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public @NotNull ReceivingOrderVO complete(@NotBlank String pKey) {
        var order = getOrder(pKey);
        if (order.getOrderState().ordinal() <= COMPLETED.ordinal()) {
            order.getPositions()
                    .stream()
                    .filter(ReceivingOrderPosition.class::isInstance)
                    .map(ReceivingOrderPosition.class::cast)
                    .forEach(p -> {
//                p.setQuantityReceived(p.getQuantityExpected());
                p.setState(COMPLETED);
            });
            order.changeOrderState(publisher, COMPLETED);
        } else {
            LOGGER.info("ReceivingOrder [{}] is not in a state to be completed", pKey);
        }
        return receivingMapper.convertToVO(repository.save(order), new CycleAvoidingMappingContext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull ReceivingOrder cancelOrder(@NotBlank String pKey) {
        var order = getOrder(pKey);
        LOGGER.info("Cancelling ReceivingOrder [{}] in state [{}]", order.getOrderId(), order.getOrderState());
        if (order.getOrderState() == CANCELED) {
            throw new AlreadyCancelledException(
                    serviceProvider.getTranslator(),
                    RO_ALREADY_IN_STATE,
                    new String[]{order.getOrderId(), order.getOrderState().name()},
                    order.getOrderId(), order.getOrderState()
            );
        }
        if (order.getOrderState() != UNDEFINED && order.getOrderState() != CREATED) {
            throw new CancellationDeniedException(
                    serviceProvider.getTranslator(),
                    RO_CANCELLATION_DENIED,
                    new String[]{order.getOrderId(), order.getOrderState().name()},
                    order.getOrderId(), order.getOrderState()
            );
        }
        order.changeOrderState(publisher, CANCELED);
        return repository.save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull ReceivingOrder changeState(@NotBlank String pKey, @NotNull OrderState state) {
        var order = getOrder(pKey);
        LOGGER.info("Change ReceivingOrder [{}] to state [{}]", order.getOrderId(), state);
        if (state == COMPLETED) {

            // Set all positions to COMPLETED
            order.getPositions().forEach(p -> p.setState(COMPLETED));
        }
        order.setOrderState(state);
        return repository.save(order);
    }

    private ReceivingOrder getOrder(@NotEmpty String pKey) {
        return repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(
                serviceProvider.getTranslator(),
                RO_NOT_FOUND_BY_PKEY,
                new String[]{pKey},
                pKey
        ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull List<ReceivingOrder> findAll() {
         return repository.findAll();
    }
}