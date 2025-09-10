package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String name,
        String version,
        SecurityProperties security,
        RabbitMQProperties rabbitmq
) {
    
    public AppProperties() {
        this(
                "medsync-cadastro-agendamento",
                "1.0.0",
                new SecurityProperties(),
                new RabbitMQProperties()
        );
    }
}
