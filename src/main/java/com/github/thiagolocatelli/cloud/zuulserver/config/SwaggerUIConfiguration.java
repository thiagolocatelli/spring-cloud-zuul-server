package com.github.thiagolocatelli.cloud.zuulserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerUIConfiguration {

    @Autowired
    private ServiceDefinitionsContext definitionContext;

    @Primary
    @Bean
    @Lazy
    public SwaggerResourcesProvider swaggerResourcesProvider(
            InMemorySwaggerResourcesProvider defaultResourcesProvider,
            @Value("${spring.application.name}") String springApplicationName) {

        return () -> {
            List<SwaggerResource> resources = new ArrayList<>(defaultResourcesProvider.get());
            resources.clear();
            resources.add(ProxyUtils.createSwaggerResource(springApplicationName, ProxyUtils.DEFAULT_SWAGGER_URL));
            resources.addAll(definitionContext.getSwaggerDefinitions());
            return resources;
        };
    }

}
