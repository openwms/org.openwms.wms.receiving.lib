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
package org.openwms.wms.receiving.impl;

import org.ameba.annotation.Measured;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.tenancy.TenantHolder;
import org.openwms.wms.receiving.ValidationGroups;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.OrderState;
import org.openwms.wms.receiving.api.PositionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static org.openwms.wms.ReceivingConstants.DEFAULT_ACCOUNT_NAME;
import static org.openwms.wms.receiving.ReceivingMessages.RO_ALREADY_EXISTS;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NOT_FOUND_BY_PKEY;
import static org.openwms.wms.receiving.api.OrderState.COMPLETED;

/**
 * A ReceivingServiceImpl is a Spring managed transactional Services that deals with {@link ReceivingOrder}s.
 * 
 * @author Heiko Scherrer
 */
@Validated
@Service
class ReceivingServiceImpl<T extends CaptureRequestVO> implements ReceivingService<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivingServiceImpl.class);
    private final Validator validator;
    private final NextReceivingOrderRepository nextReceivingOrderRepository;
    private final ReceivingOrderRepository repository;
    private final PluginRegistry<ReceivingOrderUpdater, ReceivingOrderUpdater.Type> plugins;
    private final PluginRegistry<ReceivingOrderCapturer<T>, CaptureRequestVO> capturers;
    private final ApplicationEventPublisher publisher;
    private final ServiceProvider serviceProvider;

    ReceivingServiceImpl(
            Validator validator, NextReceivingOrderRepository nextReceivingOrderRepository, ReceivingOrderRepository repository,
            @Qualifier("plugins") PluginRegistry<ReceivingOrderUpdater, ReceivingOrderUpdater.Type> plugins,
            @Qualifier("capturers") PluginRegistry<ReceivingOrderCapturer<T>, CaptureRequestVO> capturers,
            ApplicationEventPublisher publisher, ServiceProvider serviceProvider) {
        this.validator = validator;
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
    @Transactional
    public @NotNull Optional<ReceivingOrder> capture(@NotBlank String pKey, @NotNull @Valid List<T> requests) {
        Optional<ReceivingOrder> opt = Optional.empty();
        for (T request : requests) {
            opt = capturers.getPluginFor(request)
                    .orElseThrow(() -> new IllegalArgumentException("Type of CaptureRequestVO not supported"))
                    .capture(pKey, request);
        }
        if (opt.isPresent()) {
            var ro = repository.save(opt.get());
            return repository.findById(ro.getPk());
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public void captureBlindReceipts(@NotNull List<T> requests) {
        for (T request : requests) {
            capturers.getPluginFor(request)
                .orElseThrow(() -> new IllegalArgumentException("Type of CaptureRequestVO not supported"))
                .capture(null, request);
        }
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
    @Transactional
    public @NotNull ReceivingOrder update(@NotBlank String pKey, @NotNull ReceivingOrder receivingOrder) {
        var order = getOrder(pKey);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Updating ReceivingOrder [{}] with content [{}]", pKey, order);
        }
        for (var plugin : this.plugins.getPlugins()) {
            order = plugin.update(order, receivingOrder);
        }
        return repository.save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    @Transactional
    public @NotNull ReceivingOrder complete(@NotBlank String pKey) {
        LOGGER.info("Complete whole ReceivingOrder with pKey [{}]", pKey);
        var order = getOrder(pKey);
        if (order.getOrderState().ordinal() <= COMPLETED.ordinal()) {
            order.getPositions().forEach(p -> p.changePositionState(publisher, PositionState.COMPLETED));
        } else {
            LOGGER.info("ReceivingOrder [{}] is not in a state to be completed", pKey);
        }
        return repository.save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional
    public @NotNull ReceivingOrder cancelOrder(@NotBlank String pKey) {
        var order = getOrder(pKey);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to cancel ReceivingOrder [{}]", order.getOrderId());
        }
        order.cancelOrder(publisher, serviceProvider.getTranslator());
        return repository.save(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    @Transactional
    public @NotNull ReceivingOrder changeState(@NotBlank String pKey, @NotNull OrderState state) {
        var order = getOrder(pKey);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Changing ReceivingOrder [{}] to state [{}]", order.getPersistentKey(), state);
        }
        if (state != COMPLETED) {
            throw new IllegalArgumentException("Not allowed to change the state to something else than COMPLETED");
        }
        complete(pKey);
        return repository.save(order);
    }

    private ReceivingOrder getOrder(String pKey) {
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