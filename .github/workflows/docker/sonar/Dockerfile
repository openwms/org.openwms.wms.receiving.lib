FROM openwms/builder-image:latest
ARG ARGGITHUB_RUN_NUMBER
ENV GITHUB_RUN_NUMBER=$ARGGITHUB_RUN_NUMBER
ARG ARGSONAR_TOKEN
ENV SONAR_TOKEN=$ARGSONAR_TOKEN

COPY . /

CMD ./mvnw clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar -Pcoverage \
      -Dsonar.login=${SONAR_TOKEN} \
      -Dspring.rabbitmq.host=rabbitmq \
      -DsurefireArgs=-Dspring.profiles.active=ASYNCHRONOUS,TEST \
      -Dci.buildNumber=${GITHUB_RUN_NUMBER} \
      -DSONAR_TOKEN=${SONAR_TOKEN} \
      -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn
