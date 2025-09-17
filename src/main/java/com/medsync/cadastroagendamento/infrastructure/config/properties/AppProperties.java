package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private String name = "medsync-cadastro-agendamento";
    private String version = "1.0.0";
    private SecurityProperties security = new SecurityProperties();
    private RabbitMQProperties rabbitmq = new RabbitMQProperties();
    
    public AppProperties() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public SecurityProperties getSecurity() {
        return security;
    }
    
    public void setSecurity(SecurityProperties security) {
        this.security = security;
    }
    
    public RabbitMQProperties getRabbitmq() {
        return rabbitmq;
    }
    
    public void setRabbitmq(RabbitMQProperties rabbitmq) {
        this.rabbitmq = rabbitmq;
    }
}
