package com.medsync.cadastroagendamento.infrastructure.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.TimeUnit;

@Configuration
public class FeignConfig {
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
    
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(
            5000, // connectTimeout
            10000, // readTimeout
            true // followRedirects
        );
    }
    
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(
            1000, // period
            3000, // maxPeriod
            3 // maxAttempts
        );
    }
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Adicionar token JWT nas requisições Feign
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Aqui você pode extrair o token JWT do contexto de segurança
                // e adicionar no header Authorization
                requestTemplate.header("Authorization", "Bearer " + authentication.getName());
            }
        };
    }
}
