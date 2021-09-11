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
package org.openwms.wms.order;

/**
 * An OrderState is used to define the state of a ReceivingOrder or ReceivingOrderPosition.
 * 
 * @author Heiko Scherrer
 */
public enum OrderState {

    /** Initial state. */
    UNDEFINED,
    /** When the Order has been created. */
    CREATED,
    /** Not Used. */
    PROCESSING,
    /** After creation, if all position have been processed successfully. */
    PROCESSED,
    /** If the Order or Position has been cancelled. */
    CANCELED,
    /** Order or Position has been completed successfully. */
    COMPLETED
}