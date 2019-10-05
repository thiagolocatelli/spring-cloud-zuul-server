## spring-cloud-zuul-server

Simple docker container running Netflix Zuul Proxy. Spring Boot version 2.1.9-RELEASE

default port: 8080

```shell script
docker run thiagolocatelli/spring-cloud-zuul-server
```

By default, the application does not register itself with Eureka server, to enable this feature the
following environment variables need to be set:

```shell script

# enable discovery client
EUREKA_CLIENT_ENABLED=true

# set the service url for discovery client (default: http://localhost:8888/eureka)
EUREKA_CLIENT_SERVICE_URL=http://localhost:8888/eureka
```

```shell script
docker run -e EUREKA_CLIENT_ENABLED=true EUREKA_CLIENT_SERVICE_URL=http://localhost:8888/eureka thiagolocatelli/spring-cloud-zuul-serverß
```

If customizations are required for the Swagger UI, use these extra environment variables:

```shell script
INFO_APP_CONTACT_NAME
INFO_APP_CONTACT_URL
INFO_APP_CONTACT_EMAIL
INFO_APP_NAME
INFO_APP_DESCRIPTION
INFO_APP_VERSION
```