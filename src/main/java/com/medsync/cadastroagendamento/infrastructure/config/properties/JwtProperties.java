package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {
    
    private String secret;
    private long expiration;
    
    public JwtProperties() {
    }
    
    public JwtProperties(String secret, long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}
