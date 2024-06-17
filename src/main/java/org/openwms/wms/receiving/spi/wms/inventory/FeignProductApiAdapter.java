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

import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import org.ameba.annotation.Measured;
import org.ameba.system.ValidationUtil;
import org.openwms.core.SpringProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * A FeignProductApiAdapter.
 *
 * @author Heiko Scherrer
 */
@Profile(SpringProfiles.DISTRIBUTED)
@Validated
@Component
class FeignProductApiAdapter implements SyncProductApi {

    private final Validator validator;
    private final ProductApi productApi;

    FeignProductApiAdapter(Validator validator, ProductApi productApi) {
        this.validator = validator;
        this.productApi = productApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public ProductVO findBySKU(@NotBlank String sku) {
        var vo = productApi.findBySKU(sku);
        if (vo == null) {

            return null;
        }
        ValidationUtil.validate(validator, vo, ProductVO.Load.class);
        return vo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Measured
    public ProductVO findProductByProductUnitPkey(@NotBlank String pKey) {
        return productApi.findProductByProductUnitPkey(pKey);
    }
}
