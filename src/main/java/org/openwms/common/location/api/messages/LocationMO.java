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
package org.openwms.common.location.api.messages;

import java.io.Serializable;

/**
 * A LocationMO is a Message Object (MO) uses as DTO between services that represents a {@code Location}.
 *
 * @author Heiko Scherrer
 */
public record LocationMO(

        /** The business key of the {@code Location}. */
        String id,
        /** ERP code of the {@code Location}. */
        String erpCode

) implements Serializable {

    public static LocationMO ofErpCode(String erpCode) {
        return new LocationMO(null, erpCode);
    }
}
