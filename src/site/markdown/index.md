# Purpose
The purpose of the Receiving service is to handle goods receipts. It offers an API to create incoming orders, so called Receiving Orders
(expected receipts). This kind of receipts are announced before the actual goods are received. Blind receipts are supported as well. Those
don't require any announcements and allow to capture goods that are not expected to receive.

# Resources
[![Build status](https://github.com/openwms/org.openwms.wms.receiving/actions/workflows/master-build.yml/badge.svg)](https://github.com/openwms/org.openwms.wms.receiving/actions/workflows/master-build.yml)
[![Quality](https://sonarcloud.io/api/project_badges/measure?project=org.openwms:org.openwms.wms.receiving&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.openwms:org.openwms.wms.receiving)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Maven central](https://img.shields.io/maven-central/v/org.openwms/org.openwms.wms.receiving)](https://search.maven.org/search?q=a:org.openwms.wms.receiving)
[![Docker pulls](https://img.shields.io/docker/pulls/openwms/org.openwms.wms.receiving)](https://hub.docker.com/r/openwms/org.openwms.wms.receiving)
[![Join the chat at https://gitter.im/openwms/org.openwms](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/openwms/org.openwms?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**Find further Documentation on [Microservice Website](https://openwms.github.io/org.openwms.wms.receiving)**

# Build
The service can be built and started locally without any other services.

Build the code: 
```
$ ./mvnw package
```
