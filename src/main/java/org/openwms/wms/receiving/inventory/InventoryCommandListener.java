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
package org.openwms.wms.receiving.inventory;

import org.ameba.exception.NotFoundException;
import org.ameba.i18n.Translator;
import org.openwms.wms.commands.EnsureProductExistsCommand;
import org.openwms.wms.receiving.ReceivingMessages;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A InventoryCommandListener.
 *
 * @author Heiko Scherrer
 */
@Component
class InventoryCommandListener {

    private final Translator translator;
    private final ProductService service;

    InventoryCommandListener(Translator translator, ProductService service) {
        this.translator = translator;
        this.service = service;
    }

    @SuppressWarnings("squid:S2201")
    @EventListener
    public void onEnsureProductExistsCommand(EnsureProductExistsCommand command) {
        service.findBySku((String) command.getSource()).orElseThrow(
                () -> new NotFoundException(
                        translator,
                        ReceivingMessages.PRODUCT_NOT_FOUND,
                        command.getSource()
                )
        );
    }
}
