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
package org.openwms.wms.receiving.spi.wms.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * A ProductApi is the Feign client used internally, not by any business logic directly.
 *
 * @author Heiko Scherrer
 */
@FeignClient(name = "wms-inventory", decode404 = true, qualifiers = "productApi", fallback = ProductApiFallback.class)
interface ProductApi {

    /**
     * Gets {@code Product} based on {@code ProductUnit} pKey
     *
     * @param pKey The pKey of the productUnit
     */
    @GetMapping("/v1/product/product-units/{pKey}")
    ProductVO findProductByProductUnitPkey(@PathVariable("pKey") String pKey);
}
