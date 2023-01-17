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
package org.openwms.wms.receiving;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A RunSchemaGenerationTest is an empty test class to bootstrap the test ApplicationContext and run the database schema generation. It is
 * a Unit test and is executed with the surefire plugin.
 *
 * @author Heiko Scherrer
 */
@ReceivingApplicationTest
class RunSchemaGenerationTest extends AbstractTestBase {

    @Test
    void runSchemaGeneration() {
        assertEquals(this.getClass(), (RunSchemaGenerationTest.class));
    }
}
