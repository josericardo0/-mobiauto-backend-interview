FROM openjdk:21-oracle

LABEL maintainer="gmnaarea@gmail.com"

WORKDIR /app

COPY build/libs/backendinterview-0.0.1-SNAPSHOT.jar /app/backendinterview.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/backendinterview.jar"]
