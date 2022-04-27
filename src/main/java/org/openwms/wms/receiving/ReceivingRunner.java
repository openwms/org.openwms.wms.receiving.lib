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
package org.openwms.wms.receiving;

import org.ameba.app.SolutionApp;
import org.openwms.wms.receiving.inventory.Product;
import org.openwms.wms.receiving.impl.ReceivingOrder;
import org.openwms.wms.receiving.transport.TransportUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * A ReceivingRunner.
 *
 * @author Heiko Scherrer
 */
@SpringBootApplication(
        scanBasePackageClasses = {
                ReceivingRunner.class,
                SolutionApp.class
        })
@EnableJpaRepositories(basePackageClasses = {ReceivingOrder.class, TransportUnit.class, Product.class})
@EntityScan(basePackageClasses = {ReceivingOrder.class, TransportUnit.class, Product.class})
public class ReceivingRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReceivingRunner.class, args);
    }
}
