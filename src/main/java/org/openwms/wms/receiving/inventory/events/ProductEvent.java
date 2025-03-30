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
package org.openwms.wms.receiving.inventory.events;

import org.openwms.core.event.RootApplicationEvent;

import java.io.Serializable;

/**
 * A ProductEvent.
 *
 * @author Heiko Scherrer
 */
public class ProductEvent extends RootApplicationEvent implements Serializable {

    public enum TYPE {
        CREATED, UPDATED, DELETED
    }

    private TYPE type;

    public ProductEvent(Object source) {
        super(source);
    }

    public ProductEvent(Object source, TYPE type) {
        super(source);
        this.type = type;
    }

    public TYPE getType() {
        return type;
    }
}
