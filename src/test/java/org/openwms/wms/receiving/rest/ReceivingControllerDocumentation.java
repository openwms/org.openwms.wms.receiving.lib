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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.core.units.api.Piece;
import org.openwms.wms.receiving.AbstractTestBase;
import org.openwms.wms.receiving.ReceivingApplicationTest;
import org.openwms.wms.receiving.ReceivingMessages;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.LocationVO;
import org.openwms.wms.receiving.api.ProductVO;
import org.openwms.wms.receiving.api.QuantityCaptureOnLocationRequestVO;
import org.openwms.wms.receiving.api.QuantityCaptureRequestVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.openwms.wms.receiving.api.TUCaptureRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.openwms.wms.receiving.TestData.ORDER1_PKEY;
import static org.openwms.wms.receiving.api.ReceivingOrderVO.MEDIA_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A ReceivingControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@Sql("classpath:import-TEST.sql")
@ReceivingApplicationTest
@TestPropertySource(properties = "owms.receiving.unexpected-receipts-allowed=false")
class ReceivingControllerDocumentation extends AbstractTestBase {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper om;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get("/v1/receiving-orders/index")
                )
                .andExpect(status().isOk())
                .andDo(document("get-order-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_complete_order() throws Exception {
        mockMvc
                .perform(
                        post("/v1/receiving-orders/"+ORDER1_PKEY+"/complete")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("state", is("COMPLETED")))
                .andExpect(jsonPath("positions[0].state", is("COMPLETED")))
                .andExpect(jsonPath("positions[1].state", is("COMPLETED")))
                .andDo(document("order-complete", preprocessResponse(prettyPrint())))
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_cancel_order() throws Exception {
        var toLocation = createOrder("4714");
        var value = new ReceivingOrderVO("4714");
        value.setState("CANCELED");
        mockMvc
                .perform(
                        patch(toLocation)
                                .contentType(MEDIA_TYPE)
                                .content(om.writeValueAsString(value))
                )
                .andExpect(status().isOk())
                .andDo(document("order-cancel", preprocessResponse(prettyPrint())))
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_cancel_cancelled_order() throws Exception {
        var toLocation = createOrder("4715");
        var value = new ReceivingOrderVO("4715");
        value.setState("CANCELED");
        mockMvc
                .perform(
                        patch(toLocation)
                                .contentType(MEDIA_TYPE)
                                .content(om.writeValueAsString(value))
                )
                .andExpect(status().isOk())
        ;
        mockMvc
                .perform(
                        patch(toLocation)
                                .contentType(MEDIA_TYPE)
                                .content(om.writeValueAsString(value))
                )
                .andExpect(status().isGone())
                .andExpect(jsonPath("messageKey", CoreMatchers.is(ReceivingMessages.RO_ALREADY_IN_STATE)))
                .andDo(document("order-cancel-cancelled", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_NOT_cancel_order() throws Exception {
        var toLocation = createOrder("4716");
        var value = new ReceivingOrderVO("4716");
        value.setState("CANCELED");
        mockMvc
                .perform(
                        patch(toLocation)
                                .contentType(MEDIA_TYPE)
                                .content(om.writeValueAsString(value))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("messageKey", is(ReceivingMessages.RO_CANCELLATION_DENIED)))
                .andDo(document("order-cancel-403", preprocessResponse(prettyPrint())))
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_do_a_QuantityCapture() throws Exception {
        var vo = new QuantityCaptureRequestVO();
        vo.setTransportUnitId("4711");
        vo.setLoadUnitLabel("1");
        vo.setLoadUnitType("EURO");
        vo.setQuantityReceived(Piece.of(1));
        vo.setProduct(new ProductVO("C1"));
        mockMvc
                .perform(
                        post("/v1/receiving-orders/{pKey}/capture", ORDER1_PKEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new CaptureRequestVO[]{vo}))
                )
                .andDo(document("order-capture",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[]").description("Accepts multiple capture requests"),
                                fieldWithPath("[].@class").description("The type of capturing"),
                                fieldWithPath("[].transportUnitBK").description("The business key of the TransportUnit where the material has been moved to"),
                                fieldWithPath("[].loadUnitLabel").description("The identifier of the LoadUnit where the material has been put in"),
                                fieldWithPath("[].loadUnitType").optional().description("(Optional) The type of the LoadUnit, in case it must be created"),
                                fieldWithPath("[].quantity").description("The captured (received) quantity"),
                                fieldWithPath("[].quantity.*").ignored(),
                                fieldWithPath("[].quantity.unitType[]").ignored(),
                                fieldWithPath("[].product").description("The captured (received) Product"),
                                fieldWithPath("[].product.*").ignored()
                        )))
                .andExpect(status().isOk())
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_do_a_QuantityCapture_INSUFFICIENT() throws Exception {
        var vo = new QuantityCaptureRequestVO();
        vo.setTransportUnitId("4711");
        vo.setLoadUnitLabel("1");
        vo.setLoadUnitType("EURO");
        vo.setQuantityReceived(Piece.of(2));
        vo.setProduct(new ProductVO("C1"));
        mockMvc
                .perform(
                        post("/v1/receiving-orders/{pKey}/capture", ORDER1_PKEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(new CaptureRequestVO[]{vo}))
                )
                .andDo(document("order-capture-to-many", preprocessResponse(prettyPrint())))
                .andExpect(status().isConflict())
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_do_a_QuantityCapture_on_LOC() throws Exception {
        var vo = new QuantityCaptureOnLocationRequestVO();
        vo.setActualLocation(new LocationVO("WE01"));
        vo.setQuantityReceived(Piece.of(1));
        vo.setProduct(new ProductVO("C1"));
        mockMvc
                .perform(
                        post("/v1/receiving-orders/{pKey}/capture", ORDER1_PKEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(new CaptureRequestVO[]{vo}))
                )
                .andDo(document("order-capture-loc",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[]").description("Accepts multiple capture requests"),
                                fieldWithPath("[].@class").description("The type of capturing"),
                                fieldWithPath("[].actualLocation").description("The Location where the material has been moved to"),
                                fieldWithPath("[].actualLocation.erpCode").description("The business key of the Location"),
                                fieldWithPath("[].quantity").description("The captured (received) quantity"),
                                fieldWithPath("[].quantity.*").ignored(),
                                fieldWithPath("[].quantity.unitType[]").ignored(),
                                fieldWithPath("[].product").description("The captured (received) Product"),
                                fieldWithPath("[].product.*").ignored()
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.positions").isArray())
                .andExpect(jsonPath("$.positions.length()", is(3)))
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_do_a_QuantityCapture_on_LOC_INSUFFICIENT() throws Exception {
        var vo = new QuantityCaptureRequestVO();
        vo.setTransportUnitId("4711");
        vo.setLoadUnitLabel("1");
        vo.setLoadUnitType("EURO");
        vo.setQuantityReceived(Piece.of(2));
        vo.setProduct(new ProductVO("C1"));
        mockMvc
                .perform(
                        post("/v1/receiving-orders/{pKey}/capture", ORDER1_PKEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(new CaptureRequestVO[]{vo}))
                )
                .andDo(document("order-capture-loc-to-many", preprocessResponse(prettyPrint())))
                .andExpect(status().isConflict())
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_do_a_TUCapture_with_unexpected_TU() throws Exception {
        var vo = new TUCaptureRequestVO();
        vo.setTransportUnitId("00000000000000004711"); // The captured TU
        vo.setExpectedTransportUnitBK("00000000000000004712"); // The expected TU
        vo.setLoadUnitLabel("1"); // The LU id of the captured TU
        vo.setLoadUnitType("EURO"); // The LU type
        vo.setActualLocationErpCode("WE01"); // Where the goods have been captured
        mockMvc
                .perform(
                        post("/v1/receiving-orders/{pKey}/capture", ORDER1_PKEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(new CaptureRequestVO[]{vo}))
                )
                .andDo(document("order-capture-tu-unexpected", preprocessResponse(prettyPrint())))
                .andExpect(status().isConflict())
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_do_a_TUCapture_with_expected_TU() throws Exception {
        var vo = new TUCaptureRequestVO();
        vo.setTransportUnitId("00000000000000004712"); // The captured TU
        vo.setExpectedTransportUnitBK("00000000000000004712"); // The expected TU
        vo.setLoadUnitLabel("1"); // The LU id of the captured TU
        vo.setLoadUnitType("EURO"); // The LU type
        vo.setActualLocationErpCode("WE01"); // Where the goods have been captured
        mockMvc
                .perform(
                        post("/v1/receiving-orders/{pKey}/capture", ORDER1_PKEY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(new CaptureRequestVO[]{vo}))
                )
                .andDo(document("order-capture-tu", preprocessResponse(prettyPrint())))
                .andExpect(status().isOk())
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_update_order() throws Exception {
        var vo = new QuantityCaptureRequestVO();
        vo.setTransportUnitId("4711");
        vo.setLoadUnitLabel("1");
        vo.setLoadUnitType("EURO");
        vo.setQuantityReceived(Piece.of(2));
        vo.setProduct(new ProductVO("C1"));
        mockMvc
                .perform(
                        put("/v1/receiving-orders/{pKey}", ORDER1_PKEY)
                                .contentType("application/vnd.openwms.receiving-order-v1+json")
                                .content(om.writeValueAsString(vo))
                )
                .andDo(document("order-update-500", preprocessResponse(prettyPrint())))
                .andExpect(status().isOk())
        ;
    }

    private String createOrder(String orderId) throws Exception {
        var result = mockMvc
                .perform(
                        post("/v1/receiving-orders")
                                .contentType(MEDIA_TYPE)
                                .content(om.writeValueAsString(new ReceivingOrderVO(orderId)))
                )
                .andExpect(status().isCreated())
                .andReturn();

        return (String) result.getResponse().getHeaderValue(LOCATION);
    }
}
