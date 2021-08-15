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
package org.openwms.wms.receiving.impl;

import org.ameba.annotation.Measured;
import org.ameba.annotation.TxService;
import org.ameba.exception.NotFoundException;
import org.ameba.exception.ResourceExistsException;
import org.ameba.i18n.Translator;
import org.ameba.mapping.BeanMapper;
import org.ameba.tenancy.TenantHolder;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.ReceivingConstants;
import org.openwms.wms.inventory.api.AsyncPackagingUnitApi;
import org.openwms.wms.inventory.api.CreatePackagingUnitCommand;
import org.openwms.wms.inventory.api.PackagingUnitVO;
import org.openwms.wms.inventory.api.ProductVO;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.ProcessingException;
import org.openwms.wms.receiving.ReceivingOrderCreatedEvent;
import org.openwms.wms.receiving.api.CaptureDetailsVO;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.openwms.wms.ReceivingConstants.DEFAULT_ACCOUNT_NAME;
import static org.openwms.wms.order.OrderState.CANCELED;
import static org.openwms.wms.order.OrderState.COMPLETED;
import static org.openwms.wms.order.OrderState.CREATED;
import static org.openwms.wms.order.OrderState.PROCESSING;
import static org.openwms.wms.order.OrderState.UNDEFINED;
import static org.openwms.wms.receiving.ReceivingMessages.ALREADY_CANCELLED;
import static org.openwms.wms.receiving.ReceivingMessages.CANCELLATION_DENIED;

/**
 * A ReceivingServiceImpl is a Spring managed transactional Services that deals with {@link ReceivingOrder}s.
 * 
 * @author Heiko Scherrer
 */
@Validated
@TxService
class ReceivingServiceImpl implements ReceivingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceivingServiceImpl.class);
    private final boolean overbookingAllowed;
    private final Translator translator;
    private final BeanMapper mapper;
    private final NextReceivingOrderRepository nextReceivingOrderRepository;
    private final ReceivingOrderRepository repository;
    private final ProductService service;
    private final ApplicationEventPublisher publisher;
    private final AsyncPackagingUnitApi packagingUnitApi;

    ReceivingServiceImpl(
            @Value("${owms.receiving.unexpected-receipts-allowed:true}") boolean overbookingAllowed, Translator translator, BeanMapper mapper, NextReceivingOrderRepository nextReceivingOrderRepository, ReceivingOrderRepository repository,
            ProductService service, ApplicationEventPublisher publisher, AsyncPackagingUnitApi packagingUnitApi) {
        this.overbookingAllowed = overbookingAllowed;
        this.translator = translator;
        this.mapper = mapper;
        this.nextReceivingOrderRepository = nextReceivingOrderRepository;
        this.repository = repository;
        this.service = service;
        this.publisher = publisher;
        this.packagingUnitApi = packagingUnitApi;
    }

    /**
     * {@inheritDoc}
    public ReceivingOrderPosition createOrderPosition(OrderPositionKey orderPositionKey, String productId,
            UnitType quantity, String barcode) {

        // Get order data
        ReceivingOrder order = rcvOrderDao.findByOrderId(orderPositionKey.getOrderId());

        // Search Product
        Product product = productDao.findBySku(productId);

        List<LoadUnit> loadUnits = loadUnitDao.findByTransportUnit(new Barcode(barcode));
        String physicalPosition = "";
        if (!loadUnits.isEmpty()) {
            String currentMaxPosition = loadUnits.get(loadUnits.size() - 1).getPhysicalPosition();
            try {
                int val = Integer.valueOf(currentMaxPosition);
                val++;
                physicalPosition = String.valueOf(val);
            } catch (NumberFormatException e) {}
        }

        TransportUnit transportUnit = transportUnitSrv.findByBarcode(new Barcode(barcode));

        LoadUnit loadUnit = new LoadUnit(transportUnit, physicalPosition, product);

        PackagingUnit pu = new PackagingUnit(loadUnit, quantity);
        ReceivingOrderPosition rcvOrderPosition = new ReceivingOrderPosition(order, orderPositionKey.getPositionNo(),
                quantity, product);
        return wmsOrderDao.createOrderPosition(rcvOrderPosition);
    }
     */

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public @NotNull ReceivingOrder createOrder(@NotNull @Valid ReceivingOrder order) {
        Optional<ReceivingOrder> opt;
        if (order.hasOrderId()) {
            opt = repository.findByOrderId(order.getOrderId());
            if (opt.isPresent()) {
                throw new ResourceExistsException(format("The ReceivingOrder with orderId [%s] already exists", order.getOrderId()));
            }
        } else {
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
            order.setOrderId(nb.getCurrentOrderId());
        }
        order.getPositions().forEach(p -> {
            Product product = service.findBySku(p.getProduct().getSku())
                    .orElseThrow(() -> new NotFoundException(format("Product with SKU [%s] does not exist", p.getProduct().getSku())));
            p.setProduct(product);
        });
        order = repository.save(order);
        publisher.publishEvent(new ReceivingOrderCreatedEvent(order));
        return order;
    }

    private @NotNull ReceivingOrder capture(
            @NotEmpty String pKey,
            @NotEmpty String transportUnitId,
            @NotEmpty String loadUnitPosition,
            @NotEmpty String loadUnitType,
            @NotNull Measurable quantityReceived,
            CaptureDetailsVO details,
            @NotEmpty String sku) {

        //publisher.publishEvent(new EnsureProductExistsCommand(product.getSku()));
        final Product existingProduct = service.findBySku(sku).orElseThrow(
                () -> new NotFoundException(
                        translator,
                        ReceivingConstants.PRODUCT_NOT_FOUND,
                        sku
                )
        );

        ReceivingOrder receivingOrder = repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(format("ReceivingOrder with pKey [%s] does not exist", pKey)));
        List<ReceivingOrderPosition> openPositions = receivingOrder.getPositions().stream()
                .filter(p -> p.getState() == CREATED || p.getState() == PROCESSING)
                .filter(p -> p.getProduct().equals(existingProduct))
                .collect(Collectors.toList());

        if (openPositions.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded Product exist");
            throw new ProcessingException("Received a goods receipt but no open ReceivingOrderPositions with the demanded Product exist");
        }

        Optional<ReceivingOrderPosition> openPosition = openPositions.stream()
                .filter(p -> p.getQuantityExpected().getUnitType().equals(quantityReceived.getUnitType()))
                .filter(p -> p.getQuantityExpected().compareTo(quantityReceived) >= 0)
                .findFirst();
        ReceivingOrderPosition position;
        // Got an unexpected receipt. If this is configured to be okay we proceed otherwise throw
        if (openPosition.isEmpty()) {
            if (overbookingAllowed) {

                position = openPositions.get(0);
            } else {
                LOGGER.error("Received a goods receipt but all ReceivingOrderPositions are already satisfied and unexpected receipts are not allowed");
                throw new ProcessingException("Received a goods receipt but all ReceivingOrderPositions are already satisfied and unexpected receipts are not allowed");
            }
        } else {
            position = openPosition.get();
        }

        for (int i = 0; i < quantityReceived.getMagnitude().intValue(); i++) {
            PackagingUnitVO pu = new PackagingUnitVO(
                    ProductVO.newBuilder().sku(sku).build(),
                    existingProduct.getBaseUnit()
//                    Piece.of(BigDecimal.valueOf(quantityReceived.getMagnitude().intValue()))
            );
            if (details != null) {
                pu.setLength(details.getLength());
                pu.setWidth(details.getWidth());
                pu.setHeight(details.getHeight());
                if (details.getWeight() != null) {
                    pu.setWeight(details.getWeight());
                }
                pu.setMessage(details.getMessageText());
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Create new PackagingUnit [{}] on TransportUnit [{}] and LoadUnit [{}]", pu, transportUnitId, loadUnitPosition);
            }
            packagingUnitApi.create(new CreatePackagingUnitCommand(transportUnitId, loadUnitPosition, loadUnitType, pu));
        }
        position.addQuantityReceived(quantityReceived);
        receivingOrder = repository.save(receivingOrder);
        return receivingOrder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public ReceivingOrderVO capture(@NotEmpty String pKey, @NotEmpty String loadUnitType,
            @NotNull @Valid List<CaptureRequestVO> requests) {
        ReceivingOrder ro = null;
        for (CaptureRequestVO request : requests) {
            ro = this.capture(
                    pKey,
                    request.getTransportUnitId(),
                    request.getLoadUnitLabel(),
                    loadUnitType,
                    request.getQuantityReceived(),
                    request.getDetails(),
                    request.getProduct().getSku());
        }
        return mapper.map(ro, ReceivingOrderVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @Measured
    public @NotNull ReceivingOrder findByPKey(@NotEmpty String pKey) {
        Assert.hasText(pKey, "pKey must not be null");
        Optional<ReceivingOrder> order = repository.findBypKey(pKey);
        return order.orElseThrow(() -> new NotFoundException(format("ReceivingOrder with pKey [%s] does not exist", pKey)));
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Transactional(readOnly = true)
    @Override
    public Optional<ReceivingOrder> findByOrderId(@NotEmpty String orderId) {
        return repository.findByOrderId(orderId);
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public ReceivingOrder update(String pKey, ReceivingOrderVO receivingOrder) {
        ReceivingOrder order = repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(format("ReceivingOrder with pKey [%s] does not exist", pKey)));
        LOGGER.info("Updating ReceivingOrder [{}]", order.getOrderId());
        // FIXME [openwms]: 10.05.20 Implement this
        return order;
    }

    /**
     * {@inheritDoc}
     */
    @Measured
    @Override
    public ReceivingOrderVO complete(@NotEmpty String pKey) {
        ReceivingOrder order = repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(format("ReceivingOrder with pKey [%s] does not exist", pKey)));
        if (order.getOrderState().ordinal() <= COMPLETED.ordinal()) {
            order.getPositions().forEach(p -> {
                p.setQuantityReceived(p.getQuantityExpected());
                p.setState(COMPLETED);
            });
            order.setOrderState(COMPLETED);
            LOGGER.debug("ReceivingOrder [{}] with all positions completed", pKey);
        } else {
            LOGGER.info("ReceivingOrder [{}] is not in a state to be completed", pKey);
        }
        return mapper.map(order, ReceivingOrderVO.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public ReceivingOrder cancelOrder(@NotEmpty String pKey) {
        Assert.hasText(pKey, "pKey must not be null");
        ReceivingOrder order = repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(format("ReceivingOrder with pKey [%s] does not exist", pKey)));
        LOGGER.info("Cancelling ReceivingOrder [{}] in state [{}]", order.getOrderId(), order.getOrderState());
        if (order.getOrderState() == CANCELED) {
            throw new AlreadyCancelledException(
                    format("ReceivingOrder [%s] is already in state [%s]", order.getOrderId(), order.getOrderState()),
                    ALREADY_CANCELLED,
                    new String[]{order.getOrderId(), order.getOrderState().name(), order.getPersistentKey()}
            );
        }
        if (order.getOrderState() != UNDEFINED && order.getOrderState() != CREATED) {
            throw new CancellationDeniedException(
                    format("Cancellation of ReceivingOrder [%s] is not allowed because order is already in state [%s]", order.getOrderId(), order.getOrderState()),
                    CANCELLATION_DENIED,
                    new String[]{order.getOrderId(), order.getOrderState().name(), order.getPersistentKey()}
            );
        }
        order.setOrderState(CANCELED);
        return order;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public ReceivingOrder changeState(@NotEmpty String pKey, @NotNull OrderState state) {
        ReceivingOrder order = repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(format("ReceivingOrder with pKey [%s] does not exist", pKey)));
        LOGGER.info("Change ReceivingOrder [{}] to state [{}]", order.getOrderId(), state);
        if (state == COMPLETED) {

            // Set all positions to COMPLETED
            order.getPositions().forEach(p -> p.setState(COMPLETED));
        }
        order.setOrderState(state);
        return order;
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