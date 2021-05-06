FROM adoptopenjdk/openjdk11-openj9:jre-11.0.7_10_openj9-0.20.0-alpine as builder
WORKDIR app
ARG JAR_FILE=target/openwms-wms-receiving-exec.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM adoptopenjdk/openjdk11-openj9:jre-11.0.7_10_openj9-0.20.0-alpine
WORKDIR application
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/application/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
ENTRYPOINT ["java", "-Xshareclasses -Xquickstart -noverify", "org.springframework.boot.loader.JarLauncher"]