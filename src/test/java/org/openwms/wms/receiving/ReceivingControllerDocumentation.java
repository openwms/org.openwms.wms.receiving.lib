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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.core.SpringProfiles;
import org.openwms.core.units.api.Piece;
import org.openwms.wms.ReceivingApplicationTest;
import org.openwms.wms.receiving.api.ReceivingOrderPositionVO;
import org.openwms.wms.receiving.api.ReceivingOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A ReceivingControllerDocumentation.
 *
 * @author Heiko Scherrer
 */
@ActiveProfiles(SpringProfiles.ASYNCHRONOUS_PROFILE)
@ReceivingApplicationTest
class ReceivingControllerDocumentation {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper om;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(documentationConfiguration(restDocumentation)).build();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void shall_return_index() throws Exception {
        mockMvc
                .perform(
                        get("/v1/receiving/index")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.receiving-order-create").exists())
                .andExpect(jsonPath("$._links.receiving-order-findbypkey").exists())
                .andExpect(jsonPath("$._links.receiving-order-cancel").exists())
                .andExpect(jsonPath("$._links.length()", is(3)))
                .andDo(document("get-order-index", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_create_order() throws Exception {
        ReceivingOrderVO orderVO = new ReceivingOrderVO("4712");
        orderVO.getPositions().add(new ReceivingOrderPositionVO("1", null));
        mockMvc
                .perform(
                        post("/v1/receiving")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(orderVO))
                )
                .andExpect(status().isBadRequest())
                .andDo(document("order-create-400", preprocessResponse(prettyPrint())))
        ;

        orderVO.getPositions().clear();
        orderVO.getPositions().add(new ReceivingOrderPositionVO("1", Piece.of(1)));
        mockMvc
                .perform(
                        post("/v1/receiving")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(orderVO))
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, notNullValue()))
                .andDo(document("order-create", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_find_all() throws Exception {
        mockMvc
                .perform(
                        get("/v1/receiving")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", greaterThan(0)))
                .andDo(document("order-find-all", preprocessResponse(prettyPrint())))
        ;
    }

    @Transactional
    @Test void shall_find_order() throws Exception {
        MvcResult result = mockMvc
                .perform(
                        post("/v1/receiving")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(new ReceivingOrderVO("4713")))
                )
                .andExpect(status().isCreated())
                .andReturn();

        String toLocation = (String) result.getResponse().getHeaderValue(LOCATION);
        mockMvc
                .perform(
                        get(toLocation)
                )
                .andExpect(status().isOk())
                .andDo(document("order-find", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_NOT_find_order() throws Exception {
        mockMvc
                .perform(
                        get("/v1/receiving/unknown")
                )
                .andExpect(status().isNotFound())
                .andDo(document("order-find-404", preprocessResponse(prettyPrint())))
        ;
    }

    @Transactional
    @Rollback
    @Test void shall_cancel_order() throws Exception {
        String toLocation = createOrder("4714");
        mockMvc
                .perform(
                        delete(toLocation)
                )
                .andExpect(status().isNoContent())
                .andDo(document("order-cancel", preprocessResponse(prettyPrint())))
        ;
    }

    @Transactional
    @Test void shall_cancel_cancelled_order() throws Exception {
        String toLocation = createOrder("4715");
        mockMvc
                .perform(
                        delete(toLocation)
                )
                .andExpect(status().isNoContent())
        ;
        mockMvc
                .perform(
                        delete(toLocation)
                )
                .andExpect(status().isGone())
                .andExpect(jsonPath("messageKey", is(ReceivingMessages.ALREADY_CANCELLED)))
                .andDo(document("order-cancel-cancelled", preprocessResponse(prettyPrint())))
        ;
    }

    @Test void shall_NOT_cancel_order() throws Exception {
        String toLocation = createOrder("4716");
        mockMvc
                .perform(
                        delete(toLocation)
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("messageKey", is(ReceivingMessages.CANCELLATION_DENIED)))
                .andDo(document("order-cancel-403", preprocessResponse(prettyPrint())))
        ;
    }

    @Transactional
    public String createOrder(String orderId) throws Exception {
        MvcResult result = mockMvc
                .perform(
                        post("/v1/receiving")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(om.writeValueAsString(new ReceivingOrderVO(orderId)))
                )
                .andExpect(status().isCreated())
                .andReturn();

        return (String) result.getResponse().getHeaderValue(LOCATION);
    }
}
