FROM maven:alpine

MAINTAINER thiagolocatelli <thiago.locatelli@gmail.com>

EXPOSE 8080
COPY . /opt/spring-cloud-zuul-server/
WORKDIR /opt/spring-cloud-zuul-server/
RUN mvn package
VOLUME /config
WORKDIR /
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar",\
            "/opt/spring-cloud-zuul-server/target/spring-cloud-zuul-server.jar"]
