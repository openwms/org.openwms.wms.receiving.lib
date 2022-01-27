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
package org.openwms.wms.receiving.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.core.units.api.Piece;
import org.openwms.wms.receiving.AbstractTestBase;
import org.openwms.wms.receiving.ReceivingApplicationTest;
import org.openwms.wms.receiving.api.ProductVO;
import org.openwms.wms.receiving.api.ReceivingOrderPositionVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.openwms.wms.receiving.TestData.PRODUCT1_SKU;
import static org.openwms.wms.receiving.api.ReceivingOrderVO.MEDIA_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A ReceivingOrderCreatorDocumentation.
 *
 * @author Heiko Scherrer
 */
@Sql("classpath:import-TEST.sql")
@ReceivingApplicationTest
@TestPropertySource(properties = "owms.receiving.unexpected-receipts-allowed=false")
class ReceivingOrderCreatorDocumentation extends AbstractTestBase {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper om;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(documentationConfiguration(restDocumentation)).build();
    }

    @Transactional
    @Rollback
    @Test void shall_create_order() throws Exception {
        var orderVO = new ReceivingOrderVO("4712");
        var pos = new ReceivingOrderPositionVO(1, null, null);
        orderVO.getPositions().add(pos);
        mockMvc
                .perform(
                        post("/v1/receiving-orders")
                                .contentType(MEDIA_TYPE)
                                .content(om.writeValueAsString(orderVO))
                )
                .andExpect(status().isBadRequest())
                .andDo(document("order-create-400", preprocessResponse(prettyPrint())))
        ;

        orderVO.getPositions().clear();
        var sku001 = new ReceivingOrderPositionVO(1, Piece.of(1), new ProductVO(PRODUCT1_SKU));
        sku001.getDetails().put("p1", "v1");
        orderVO.getPositions().add(sku001);
        mockMvc
                .perform(
                        post("/v1/receiving-orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(orderVO))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, notNullValue()))
                .andDo(document("order-create",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("orderId").description("An unique identifier of the ReceivingOrder to create, if not provided the server generates an ID"),
                                fieldWithPath("positions[]").description("An array of positions, must not be empty"),
                                fieldWithPath("positions[].@class").description("The type of the ReceivingOrderPosition"),
                                fieldWithPath("positions[].positionId").description("Unique identifier of the ReceivingOrderPosition within the ReceivingOrder"),
                                fieldWithPath("positions[].quantityExpected").description("The expected quantity of the Product"),
                                fieldWithPath("positions[].quantityExpected.@class").description("Must be one of the static values to identify the type of UOM"),
                                fieldWithPath("positions[].quantityExpected.unitType").description("Must be one of the static values to identify the concrete UOM"),
                                fieldWithPath("positions[].quantityExpected.magnitude").description("The amount"),
                                fieldWithPath("positions[].product.sku").description("The SKU of the expected Product"),
                                fieldWithPath("positions[].details").description("Some arbitrary detail information of the position"),
                                fieldWithPath("positions[].details.p1").ignored()//,
                                //fieldWithPath("links").description("Further action links provided on the ReceivingOrder resource")
                                )
                        )
                )
        ;
    }
}
