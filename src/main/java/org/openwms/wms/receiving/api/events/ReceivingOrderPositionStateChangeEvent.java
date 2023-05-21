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
package org.openwms.wms.receiving.api.events;

import org.openwms.core.event.RootApplicationEvent;
import org.openwms.wms.receiving.api.PositionState;
import org.openwms.wms.receiving.impl.AbstractReceivingOrderPosition;

import java.io.Serializable;

/**
 * A ReceivingOrderPositionStateChangeEvent.
 * 
 * @author Heiko Scherrer
 */
public class ReceivingOrderPositionStateChangeEvent<T extends AbstractReceivingOrderPosition> extends RootApplicationEvent implements Serializable {

    public final PositionState state;

    public ReceivingOrderPositionStateChangeEvent(T source, PositionState state) {
        super(source);
        this.state = state;
    }

    @Override
    public T getSource() {
        return (T) super.getSource();
    }

    public PositionState getState() {
        return state;
    }
}