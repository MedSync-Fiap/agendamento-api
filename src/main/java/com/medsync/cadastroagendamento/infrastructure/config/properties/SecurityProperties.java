package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        JwtProperties jwt
) {
    
    public SecurityProperties() {
        this(new JwtProperties("mySecretKey123456789012345678901234567890", 86400000L));
    }
}
