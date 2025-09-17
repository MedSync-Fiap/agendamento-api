package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    
    private JwtProperties jwt = new JwtProperties("mySecretKey123456789012345678901234567890", 86400000L);
    
    public SecurityProperties() {
    }
    
    public JwtProperties getJwt() {
        return jwt;
    }
    
    public void setJwt(JwtProperties jwt) {
        this.jwt = jwt;
    }
}
