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
import org.ameba.tenancy.TenantHolder;
import org.openwms.core.units.api.Measurable;
import org.openwms.wms.inventory.api.AsyncPackagingUnitApi;
import org.openwms.wms.inventory.api.CreatePackagingUnitCommand;
import org.openwms.wms.inventory.api.PackagingUnitVO;
import org.openwms.wms.inventory.api.ProductVO;
import org.openwms.wms.order.OrderState;
import org.openwms.wms.receiving.CycleAvoidingMappingContext;
import org.openwms.wms.receiving.ProcessingException;
import org.openwms.wms.receiving.ReceivingMapper;
import org.openwms.wms.receiving.ReceivingMessages;
import org.openwms.wms.receiving.api.CaptureDetailsVO;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.QuantityCaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.api.TUCaptureRequestVO;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.inventory.ProductService;
import org.openwms.wms.receiving.transport.api.TransportUnitApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.openwms.wms.ReceivingConstants.DEFAULT_ACCOUNT_NAME;
import static org.openwms.wms.order.OrderState.CANCELED;
import static org.openwms.wms.order.OrderState.COMPLETED;
import static org.openwms.wms.order.OrderState.CREATED;
import static org.openwms.wms.order.OrderState.PROCESSING;
import static org.openwms.wms.order.OrderState.UNDEFINED;
import static org.openwms.wms.receiving.ReceivingMessages.RO_ALREADY_EXISTS;
import static org.openwms.wms.receiving.ReceivingMessages.RO_ALREADY_IN_STATE;
import static org.openwms.wms.receiving.ReceivingMessages.RO_CANCELLATION_DENIED;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NOT_FOUND_BY_PKEY;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_OPEN_POSITIONS;
import static org.openwms.wms.receiving.ReceivingMessages.RO_NO_UNEXPECTED_ALLOWED;

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
    private final ReceivingMapper receivingMapper;
    private final NextReceivingOrderRepository nextReceivingOrderRepository;
    private final ReceivingOrderRepository repository;
    private final ProductService service;
    private final ApplicationEventPublisher publisher;
    private final AsyncPackagingUnitApi packagingUnitApi;
    private final TransportUnitApi transportUnitApi;

    ReceivingServiceImpl(
            @Value("${owms.receiving.unexpected-receipts-allowed:true}") boolean overbookingAllowed,
            Translator translator, ReceivingMapper receivingMapper,
            NextReceivingOrderRepository nextReceivingOrderRepository, ReceivingOrderRepository repository,
            ProductService service, ApplicationEventPublisher publisher, AsyncPackagingUnitApi packagingUnitApi,
            TransportUnitApi transportUnitApi) {
        this.overbookingAllowed = overbookingAllowed;
        this.translator = translator;
        this.receivingMapper = receivingMapper;
        this.nextReceivingOrderRepository = nextReceivingOrderRepository;
        this.repository = repository;
        this.service = service;
        this.publisher = publisher;
        this.packagingUnitApi = packagingUnitApi;
        this.transportUnitApi = transportUnitApi;
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
                throw new ResourceExistsException(translator, RO_ALREADY_EXISTS, new String[]{order.getOrderId()}, order.getOrderId());
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
            order.setOrderId(nb.getCompleteOrderId());
        }
        order.getPositions().stream()
                .filter(p -> p instanceof ReceivingOrderPosition)
                .forEach(p -> ((ReceivingOrderPosition) p).setProduct(getProduct(((ReceivingOrderPosition) p).getProduct().getSku())));
        order = repository.save(order);
        publisher.publishEvent(new ReceivingOrderCreatedEvent(order));
        return order;
    }

    private Product getProduct(String sku) {
        return service.findBySku(sku).orElseThrow(
                () -> new NotFoundException(
                        translator,
                        ReceivingMessages.PRODUCT_NOT_FOUND,
                        sku
                ));
    }

    private @NotNull ReceivingOrder capture(
            @NotEmpty String pKey,
            @NotEmpty String transportUnitId,
            @NotEmpty String loadUnitPosition,
            @NotEmpty String loadUnitType,
            @NotNull Measurable quantityReceived,
            CaptureDetailsVO details,
            @NotEmpty String sku) {

        final Product existingProduct = getProduct(sku);
        ReceivingOrder receivingOrder = getOrder(pKey);
        List<ReceivingOrderPosition> openPositions = receivingOrder.getPositions().stream()
                .filter(p -> p.getState() == CREATED || p.getState() == PROCESSING)
                .filter(p -> p instanceof ReceivingOrderPosition)
                .map(p -> (ReceivingOrderPosition) p)
                .filter(p -> p.getProduct().equals(existingProduct))
                .collect(Collectors.toList());

        if (openPositions.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingOrderPositions with the demanded Product exist");
            throw new ProcessingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
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
                throw new ProcessingException(translator, RO_NO_UNEXPECTED_ALLOWED, new String[0]);
            }
        } else {
            position = openPosition.get();
        }

        for (int i = 0; i < quantityReceived.getMagnitude().intValue(); i++) {
            // single packs
            PackagingUnitVO pu = new PackagingUnitVO(
                    ProductVO.newBuilder().sku(sku).build(),
                    existingProduct.getBaseUnit()
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
        /*
        int compared = position.getQuantityReceived().compareTo(position.getQuantityExpected());
        if (compared >= 0){
            position.setState(COMPLETED);
        }
        if (receivingOrder.getPositions().stream().noneMatch(rop -> rop.getState() != COMPLETED)) {
            receivingOrder.changeOrderState(publisher, COMPLETED);
        }
         */
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
            if (request instanceof QuantityCaptureRequestVO) {
                var qr = (QuantityCaptureRequestVO) request;
                ro = this.capture(
                        pKey,
                        qr.getTransportUnitId(),
                        qr.getLoadUnitLabel(),
                        loadUnitType,
                        qr.getQuantityReceived(),
                        qr.getDetails(),
                        qr.getProduct().getSku());
            } else if (request instanceof TUCaptureRequestVO) {
                var tur = (TUCaptureRequestVO) request;
                ro = this.capture(
                        pKey,
                        tur.getExpectedTransportUnitBK(),
                        tur.getActualLocationErpCode(),
                        loadUnitType,
                        tur.getDetails()
                );
            } else {
                throw new IllegalArgumentException("Type not supported");
            }
        }
        return receivingMapper.convertToVO(ro, new CycleAvoidingMappingContext());
    }

    private @NotNull ReceivingOrder capture(String pKey, String expectedTransportUnitBK, String actualLocationErpCode,
                                            String loadUnitType, CaptureDetailsVO details) {
        ReceivingOrder receivingOrder = getOrder(pKey);
        Optional<ReceivingTransportUnitOrderPosition> openPosition = receivingOrder.getPositions().stream()
                .filter(p -> p.getState() == CREATED || p.getState() == PROCESSING)
                .filter(p -> p instanceof ReceivingTransportUnitOrderPosition)
                .map(p -> (ReceivingTransportUnitOrderPosition) p)
                .filter(p -> p.getTransportUnitBK().equals(expectedTransportUnitBK))
                .findFirst();

        if (openPosition.isEmpty()) {
            LOGGER.error("Received a goods receipt but no open ReceivingTransportUnitOrderPosition with the demanded TransportUnit exist");
            throw new ProcessingException(translator, RO_NO_OPEN_POSITIONS, new String[0]);
        }
        LOGGER.info("Received expected TransportUnit [{}] with ReceivingOrder [{}] in ReceivingOrderPosition [{}]",
                expectedTransportUnitBK, receivingOrder.getOrderId(), openPosition.get().getPosNo());
        openPosition.get().changeOrderState(publisher, COMPLETED);
        if (receivingOrder.getPositions().stream().allMatch(rop -> rop.getState() == COMPLETED)) {
            receivingOrder.changeOrderState(publisher, COMPLETED);
        }
        receivingOrder = repository.save(receivingOrder);
        transportUnitApi.moveTU(expectedTransportUnitBK, actualLocationErpCode);
        return receivingOrder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @Measured
    public @NotNull ReceivingOrder findByPKey(@NotEmpty String pKey) {
        return getOrder(pKey);
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
        ReceivingOrder order = getOrder(pKey);
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
        ReceivingOrder order = getOrder(pKey);
        if (order.getOrderState().ordinal() <= COMPLETED.ordinal()) {
            order.getPositions()
                    .stream()
                    .filter(p -> p instanceof ReceivingOrderPosition)
                    .map(p -> (ReceivingOrderPosition) p)
                    .forEach(p -> {
//                p.setQuantityReceived(p.getQuantityExpected());
                p.setState(COMPLETED);
            });
            order.changeOrderState(publisher, COMPLETED);
        } else {
            LOGGER.info("ReceivingOrder [{}] is not in a state to be completed", pKey);
        }
        return receivingMapper.convertToVO(order, new CycleAvoidingMappingContext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public ReceivingOrder cancelOrder(@NotEmpty String pKey) {
        ReceivingOrder order = getOrder(pKey);
        LOGGER.info("Cancelling ReceivingOrder [{}] in state [{}]", order.getOrderId(), order.getOrderState());
        if (order.getOrderState() == CANCELED) {
            throw new AlreadyCancelledException(
                    translator,
                    RO_ALREADY_IN_STATE,
                    new String[]{order.getOrderId(), order.getOrderState().name()},
                    order.getOrderId(), order.getOrderState()
            );
        }
        if (order.getOrderState() != UNDEFINED && order.getOrderState() != CREATED) {
            throw new CancellationDeniedException(
                    translator,
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
    public ReceivingOrder changeState(@NotEmpty String pKey, @NotNull OrderState state) {
        ReceivingOrder order = getOrder(pKey);
        LOGGER.info("Change ReceivingOrder [{}] to state [{}]", order.getOrderId(), state);
        if (state == COMPLETED) {

            // Set all positions to COMPLETED
            order.getPositions().forEach(p -> p.setState(COMPLETED));
        }
        order.setOrderState(state);
        return order;
    }

    private ReceivingOrder getOrder(@NotEmpty String pKey) {
        return repository.findBypKey(pKey).orElseThrow(() -> new NotFoundException(
                translator,
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