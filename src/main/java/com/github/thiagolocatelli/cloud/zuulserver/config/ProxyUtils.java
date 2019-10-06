package com.github.thiagolocatelli.cloud.zuulserver.config;

import springfox.documentation.swagger.web.SwaggerResource;

public class ProxyUtils {

    public static final String DEFAULT_SWAGGER_URL = "/v2/api-docs";
    public static final String DEFAULT_SWAGGER_VERSION = "2.0";
    public static final String KEY_SWAGGER_URL = "swagger_url";
    public static final String KEY_SWAGGER_PATH = "swagger-path";
    public static final String EMPTY_STRING = "";

    public static SwaggerResource createSwaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(DEFAULT_SWAGGER_VERSION);
        return swaggerResource;
    }

}
