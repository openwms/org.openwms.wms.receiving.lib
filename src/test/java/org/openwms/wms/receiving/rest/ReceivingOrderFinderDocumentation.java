/*
 * Copyright 2005-2025 the original author or authors.
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.wms.receiving.AbstractTestBase;
import org.openwms.wms.receiving.ReceivingApplicationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.openwms.wms.receiving.TestData.ORDER1_PKEY;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A ReceivingOrderFinderDocumentation.
 *
 * @author Heiko Scherrer
 */
@Sql("classpath:import-TEST.sql")
@ReceivingApplicationTest
@TestPropertySource(properties = "owms.receiving.unexpected-receipts-allowed=false")
class ReceivingOrderFinderDocumentation extends AbstractTestBase {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test void shall_find_all() throws Exception {
        mockMvc
                .perform(
                        get("/v1/receiving-orders")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", greaterThan(0)))
                .andDo(document("order-find-all", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_find_order() throws Exception {
        mockMvc
                .perform(
                        get("/v1/receiving-orders/" + ORDER1_PKEY)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.length()", equalTo(2)))
                .andExpect(jsonPath("$.positions[0].details.length()", equalTo(3)))
                .andDo(document("order-find",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("pKey").description("The synthetic unique identifier of the ReceivingOrder"),
                                fieldWithPath("orderId").description("The business key of the ReceivingOrder"),
                                fieldWithPath("state").description("The current state of the ReceivingOrder"),
                                fieldWithPath("ol").ignored(),
                                fieldWithPath("details").description("Stores arbitrary detail information according to the ReceivingOrder"),
                                fieldWithPath("details.*").ignored(),
                                fieldWithPath("positions[].positionId").description("The position of the ReceivingOrderPosition"),
                                fieldWithPath("positions[].priority").description("The current priority of the ReceivingOrder the position belongs to"),
                                fieldWithPath("positions[].@class").description("The type of the ReceivingOrderPosition"),
                                fieldWithPath("positions[].state").description("The state of the ReceivingOrderPosition"),
                                fieldWithPath("positions[].quantityExpected").optional().description("The expected quantity to be received"),
                                fieldWithPath("positions[].quantityExpected.magnitude").description("The expected quantity amount"),
                                fieldWithPath("positions[].quantityExpected.unitType").description("The expected quantity type"),
                                fieldWithPath("positions[].quantityExpected.*").ignored(),
                                fieldWithPath("positions[].quantityReceived").optional().description("The already received quantity"),
                                fieldWithPath("positions[].quantityReceived.magnitude").description("The received quantity amount"),
                                fieldWithPath("positions[].quantityReceived.unitType").description("The received quantity type"),
                                fieldWithPath("positions[].quantityReceived.*").ignored(),
                                fieldWithPath("positions[].details").optional().description("Stores arbitrary detail information according to the ReceivingOrderPosition"),
                                fieldWithPath("positions[].details.*").optional().ignored(),
                                fieldWithPath("positions[].transportUnitBK").optional().description("The business key of the captured TransportUnit"),
                                fieldWithPath("positions[].transportUnitTypeName").optional().description("The name of the captured TransportUnitType"),
                                fieldWithPath("positions[].product").optional().description("The expected Product to be received"),
                                fieldWithPath("positions[].product.baseUnit").description("The default unit and quantity the Product is shipped"),
                                fieldWithPath("positions[].product.baseUnit.unitType").description("The default unit type of the Product"),
                                fieldWithPath("positions[].product.baseUnit.*").ignored(),
                                fieldWithPath("positions[].product.*").ignored(),
                                fieldWithPath("positions[].orderId").optional().description("The unique id of the ReceivingOrder the position belongs to"),
                                fieldWithPath("positions[].createDt").description("Timestamp when the position has been created."),
                                fieldWithPath("createDt").description("Timestamp when the ReceivingOrder has been created.")
                        )
                ))
        ;
    }

    @Test void shall_find_orderBy_BK() throws Exception {
        mockMvc
                .perform(
                        get("/v1/receiving-orders").param("orderId", "T4711")
                )
                .andExpect(status().isOk())
                .andDo(document("order-findby-orderid", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_NOT_find_order() throws Exception {
        mockMvc
                .perform(
                        get("/v1/receiving-orders/unknown")
                )
                .andExpect(status().isNotFound())
                .andDo(document("order-find-404", preprocessResponse(prettyPrint())))
        ;
    }
}
