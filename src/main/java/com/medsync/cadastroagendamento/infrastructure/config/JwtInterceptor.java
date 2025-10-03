package com.medsync.cadastroagendamento.infrastructure.config;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
public class JwtInterceptor implements ExchangeFilterFunction {
    
    private final JwtConfig jwtConfig;
    
    public JwtInterceptor(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }
    
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        // Gerar token JWT para comunicação entre serviços
        String serviceToken = jwtConfig.generateServiceToken();
        
        // Adicionar token no header Authorization
        ClientRequest authenticatedRequest = ClientRequest.from(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceToken)
                .build();
        
        return next.exchange(authenticatedRequest);
    }
}
