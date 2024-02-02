FROM openjdk:21-oracle

LABEL maintainer="gmnaarea@gmail.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=backendinterview-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} /app/backendinterview.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/backendinterview.jar"]
