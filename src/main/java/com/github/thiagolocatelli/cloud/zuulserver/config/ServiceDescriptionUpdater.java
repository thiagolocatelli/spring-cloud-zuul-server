package com.github.thiagolocatelli.cloud.zuulserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger.web.SwaggerResource;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
public class ServiceDescriptionUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDescriptionUpdater.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ServiceDefinitionsContext definitionContext;

    private final RestTemplate restTemplate;

    public ServiceDescriptionUpdater(RestTemplateBuilder restTemplateBuilder,
                                     @Value("${proxy.gateway.swagger.config.connect.timeout:5000}") int connectTimeout,
                                     @Value("${proxy.gateway.swagger.config.read.timeout:5000}") int readTimeout) {

        LOGGER.debug("RestTemplate Connect timeout is {}ms, Read timeout is {}ms", connectTimeout, readTimeout);

        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    @Scheduled(fixedDelayString = "${proxy.gateway.swagger.config.refreshrate:120000}", initialDelay = 0)
    public void refreshSwaggerConfigurationsFromDiscoveryClient() {
        long processingStart = System.currentTimeMillis();
        LOGGER.info("Service Definition refresh started", (System.currentTimeMillis() - processingStart));

        for (String aServiceId : discoveryClient.getServices()) {
            // Skip consul entry
            if (aServiceId.equals("discovery-server") || aServiceId.equals("zuul-server") || aServiceId.equals("gateway-server")) {
                continue;
            }

            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(aServiceId);
            if (serviceInstances != null && !serviceInstances.isEmpty()) {
                ServiceInstance serviceInstance = serviceInstances.get(0);
                String swaggerURL = getSwaggerURL(serviceInstance, false);
                Optional<Object> jsonData = getSwaggerDefinitionForAPI(aServiceId, swaggerURL);
                if (jsonData.isPresent()) {
                    LOGGER.info("Loading service definition: {}", aServiceId);
                    definitionContext.addServiceDefinition(aServiceId, "/" + aServiceId +
                            getSwaggerPath(serviceInstance));
                } else {
                    LOGGER.info("Removing service definition: {}", aServiceId);
                    definitionContext.removeServiceDefinition(aServiceId);
                }
            }
        }

        // Remove any Service Definitions that no longer exist in the discovery server.
        for (SwaggerResource aSwaggerDefinition : definitionContext.getSwaggerDefinitions()) {
            String serviceId = aSwaggerDefinition.getName();
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
            if (serviceInstances == null || serviceInstances.isEmpty()) {
                definitionContext.removeServiceDefinition(serviceId);
            }
        }

        LOGGER.info("Service Definition refresh took {}ms", (System.currentTimeMillis() - processingStart));
    }

    private String getSwaggerPath(ServiceInstance serviceInstance) {
        String swaggerPath = serviceInstance.getMetadata().get(ProxyUtils.KEY_SWAGGER_PATH);
        if (swaggerPath != null && !swaggerPath.equals(ProxyUtils.EMPTY_STRING)) {
            return swaggerPath;
        }

        return ProxyUtils.DEFAULT_SWAGGER_URL;
    }

    private String getSwaggerURL(ServiceInstance instance, boolean includeContextPath) {
        String swaggerURL = instance.getMetadata().get(ProxyUtils.KEY_SWAGGER_URL);
        return swaggerURL != null ? instance.getUri() + "/" + instance.getServiceId() + swaggerURL :
                instance.getUri() + (includeContextPath ? "/" + instance.getServiceId() : "") + ProxyUtils.DEFAULT_SWAGGER_URL;
    }

    private Optional<Object> getSwaggerDefinitionForAPI(String serviceName, String url) {
        try {
            Object jsonData = restTemplate.getForObject(url, Object.class);

            return Optional.ofNullable(jsonData);
        } catch (RestClientException ex) {
            LOGGER.error("Error while getting service definition for service '{}'. Error : {} ", serviceName, ex.getMessage());
            return Optional.empty();
        }
    }

}
