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
package org.openwms.wms.receiving;

/**
 * A ReceivingMessages.
 *
 * @author Heiko Scherrer
 */
public final class ReceivingMessages {

    public static final String PRODUCT_NOT_FOUND = "owms.wms.rec.product404";

    public static final String RO_NOT_FOUND_BY_PKEY = "owms.wms.rec.recOrderNotFound";
    public static final String RO_NOT_FOUND_BY_BK = "owms.wms.rec.recOrderNotFoundBK";
    public static final String RO_ALREADY_IN_STATE = "owms.wms.rec.recOrderAlreadyInState";
    public static final String RO_PARTIAL_COMPLETION_DENIED = "owms.wms.rec.recOrderPartialCompletionDenied";
    public static final String RO_CANCELLATION_DENIED = "owms.wms.rec.recOrderCancellationDenied";
    public static final String RO_ALREADY_EXISTS = "owms.wms.rec.recOrderExists";
    public static final String RO_NO_OPEN_POSITIONS = "owms.wms.rec.recOrderNoROPWithProduct";
    public static final String RO_NO_OPEN_POSITIONS_TU = "owms.wms.rec.recOrderNoROPWithTU";
    public static final String RO_NO_UNEXPECTED_ALLOWED = "owms.wms.rec.recOrderNoUnexpectedAllowed";

    private ReceivingMessages() {
    }
}
