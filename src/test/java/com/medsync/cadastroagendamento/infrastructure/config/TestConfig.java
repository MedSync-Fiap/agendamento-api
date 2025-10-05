package com.medsync.cadastroagendamento.infrastructure.config;

import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import com.medsync.cadastroagendamento.infrastructure.config.properties.SecurityProperties;
import com.medsync.cadastroagendamento.infrastructure.config.properties.JwtProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public AppProperties appProperties() {
        AppProperties appProperties = new AppProperties();
        
        SecurityProperties securityProperties = new SecurityProperties();
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret("testSecretKey123456789012345678901234567890");
        jwtProperties.setExpiration(86400000L);
        securityProperties.setJwt(jwtProperties);
        
        appProperties.setSecurity(securityProperties);
        
        return appProperties;
    }
}
