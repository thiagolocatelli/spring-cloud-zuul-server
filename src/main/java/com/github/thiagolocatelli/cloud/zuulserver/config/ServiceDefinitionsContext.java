package com.github.thiagolocatelli.cloud.zuulserver.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ServiceDefinitionsContext {

    private final ConcurrentHashMap<String, String> serviceDescriptions;

    private ServiceDefinitionsContext() {
        serviceDescriptions = new ConcurrentHashMap<>();
    }

    public void addServiceDefinition(String serviceName, String serviceDescription) {
        serviceDescriptions.put(serviceName, serviceDescription);
    }

    public void removeServiceDefinition(String serviceName) {
        serviceDescriptions.remove(serviceName);
    }

    public String getSwaggerDefinition(String serviceId) {
        return this.serviceDescriptions.get(serviceId);
    }

    public List<SwaggerResource> getSwaggerDefinitions() {
        return serviceDescriptions.entrySet().stream().map(
                serviceDefinition -> ProxyUtils.createSwaggerResource(serviceDefinition.getKey(), serviceDefinition.getValue())
        ).sorted((o1, o2)-> o1.getName().compareTo(o2.getName())).collect(Collectors.toList());
    }

}
