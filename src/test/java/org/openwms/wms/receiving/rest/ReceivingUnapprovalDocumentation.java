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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openwms.core.units.api.Piece;
import org.openwms.wms.receiving.AbstractTestBase;
import org.openwms.wms.receiving.ReceivingApplicationTest;
import org.openwms.wms.receiving.api.CaptureRequestVO;
import org.openwms.wms.receiving.api.LocationVO;
import org.openwms.wms.receiving.api.ProductVO;
import org.openwms.wms.receiving.api.QuantityCaptureOnLocationRequestVO;
import org.openwms.wms.receiving.spi.wms.receiving.CapturingApproval;
import org.openwms.wms.receiving.spi.wms.receiving.NotApprovedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.openwms.wms.receiving.TestData.ORDER1_PKEY;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A ReceivingUnapprovalDocumentation.
 *
 * @author Heiko Scherrer
 */
@Sql("classpath:import-TEST.sql")
@ReceivingApplicationTest
@Import(ReceivingUnapprovalDocumentation.UnapprovalTestConfiguration.class)
class ReceivingUnapprovalDocumentation extends AbstractTestBase {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper om;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(documentationConfiguration(restDocumentation)).build();
    }

    @TestConfiguration
    public static class UnapprovalTestConfiguration {
        @Bean
        public CapturingApproval<QuantityCaptureOnLocationRequestVO> TestCapturerApproval() {
            return (receivingOrderPosition, request) -> {
                throw new NotApprovedException("Position [1] of order ["+ORDER1_PKEY+"] not approved", "MSG_001", new String[]{ORDER1_PKEY});
            };
        }
    }

    @Test void shall_do_a_QuantityCapture_on_LOC_unapproved() throws Exception {
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
                .andExpect(status().isForbidden())
                .andDo(document("order-capture-loc-403", preprocessResponse(prettyPrint())))
        ;
    }
}
