FROM maven:3.6.2-jdk-11-slim AS builder
MAINTAINER thiagolocatelli <thiago.locatelli@gmail.com>
COPY . /application/
WORKDIR /application/
RUN mvn package

FROM openjdk:11.0.4-jre
MAINTAINER thiagolocatelli <thiago.locatelli@gmail.com>
EXPOSE 8080
WORKDIR /application/
COPY --from=builder /application/target/spring-cloud-zuul-server.jar /application/spring-cloud-zuul-server.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/application/spring-cloud-zuul-server.jar"]