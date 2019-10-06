package com.github.thiagolocatelli.cloud.zuulserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Autowired
    private Environment environment;

    @Bean
    public Docket api() {

        Contact contact = new Contact(
                this.environment.getProperty("info.app.contact.name"),
                this.environment.getProperty("info.app.contact.url"),
                this.environment.getProperty("info.app.contact.email"));

        ApiInfo apiInfo = new ApiInfo(
                this.environment.getProperty("info.app.name"),
                this.environment.getProperty("info.app.description"),
                this.environment.getProperty("info.app.version"),
                null,
                contact,
                "MIT",
                "https://opensource.org/licenses/MIT",
                Collections.emptyList());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/api.*|/actuator/.*"))
                .build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Allow anyone and anything access. Probably ok for Swagger spec
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/v2/api-docs", config);
        return new CorsFilter(source);
    }

}
