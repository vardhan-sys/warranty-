FROM dockerhub.artifactory.av.ge.com/openjdk:8u212-alpine
LABEL supportemail="dossier_pubs@ge.com"
LABEL uai="uai1003619"
RUN mkdir /app
COPY ./target/*.jar /app/av-cp-techpubs.jar
ADD https://download.newrelic.com/newrelic/java-agent/newrelic-agent/7.5.0/newrelic-agent-7.5.0.jar /app/newrelic.jar
RUN touch /app/newrelic.yml
ENV LANG en_GB.UTF-8
RUN apk add --update ttf-dejavu && rm -rf /var/cache/apk/*
EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -javaagent:/app/newrelic.jar -jar /app/av-cp-techpubs.jar" ]
