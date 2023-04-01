## Configuration
OpenWMS.org defines additional configuration parameters beside the standard Spring Framework ones. All custom parameters are children of the
`owms` property namespace.

|Parameter|Type|Default profile value|Description|
|---------|----|-----------|
|owms.eureka.url|string|http://user:sa@localhost:8761|The base URL of the running Eureka service discovery server, inclusive schema and port|
|owms.eureka.zone|string|http://user:sa@localhost:8761/eureka/|The full Eureka registration endpoint URL|
|owms.service.protocol|string|http|The protocol the service' is accessible from Eureka clients|  
|owms.service.hostname|string|localhost|The hostname the service' is accessible from Eureka clients|
|owms.dead-letter.exchange-name|string|dle.common|Exchange for the poison message forwarding|
|owms.dead-letter.queue-name|string|common-dle-common|Queue for poison messages bound to the dead-letter exchange|
|owms.commands.common.tu.exchange-name|string|common.tu.commands|Exchange to send out TU requests|
|owms.commands.inventory.pu.exchange-name|string|inventory.commands|Exchange to send out PU requests|
|owms.commands.inventory.pu.routing-key|string|pu.command.create|Routing key for commands to create PU|
|owms.events.receiving.exchange-name|string|receiving|Exchange to send out events on ReceivingOrders|
|owms.events.inventory.exchange-name|string|inventory|Exchange to listen on Inventory events|
|owms.events.inventory.products.queue-name|string|receiving-products-queue|Queue to receive Product events|
|owms.events.inventory.products.routing-key|string|product.event.#|Routing key to listen on Product events|
|owms.events.common.tu.exchange-name|string|common.tu|Exchange to listen on Common events|
|owms.events.common.tu.routing-key|string|tu.event.#|Routing key to listen on TransportUnit events|
|owms.events.common.tu.queue-name|string|receiving-tu-queue|Queue to receive TransportUnit events|
|owms.receiving.serialization|string|json|The AMQP message exchange format, either `json` or `binary`|
|owms.receiving.create-tu-on-expected-tu-receipt|boolean|true|A TransportUnit with the expected BK is created when captured|
|owms.receiving.initial-location-id|string|EXTERN|The ERP code of the Location where the expected TransportUnit is created on|
|owms.receiving.blind-receipts.allowed|boolean|true|Whether blind receipts are allowed|
