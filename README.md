# Purpose
This service is responsible to handle incoming orders, so called receiving orders, aka expected receipts or receiving notices. Before a
pallet or box enters a warehouse it must be announced beforehand. The box must be registered in the system with an identifier, and some
other characteristics that may differ between projects.

# Build
The service can be built and run locally without any other services.

Build the code: 
```
$ ./mvnw package
```

# Run Standalone
Run in standalone mode:
```
$ java -jar target/openwms-wms-receiving-exec.jar 
```

# Run Distributed
Or additionally run in a distributed environment with an already running Service Registry, Configuration Server and a RabbitMQ broker:
```
$ java -Dspring.profiles.active=ASYNCHRONOUS,DEMO -jar target/openwms-wms-receiving-exec.jar 
```

# Resources

[![Build status](https://travis-ci.com/openwms/org.openwms.wms.receiving.svg?branch=master)](https://travis-ci.com/openwms/org.openwms.wms.receiving)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.wms.receiving&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.wms.receiving)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**Find further Documentation on [Microservice Website](https://openwms.github.io/org.openwms.wms.receiving)**

