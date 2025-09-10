package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PublicarEventoNotificacaoUseCase {
    
    private final RabbitTemplate rabbitTemplate;
    private final AppProperties appProperties;
    
    public PublicarEventoNotificacaoUseCase(RabbitTemplate rabbitTemplate, AppProperties appProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.appProperties = appProperties;
    }
    
    public void executar(Object evento) {
        rabbitTemplate.convertAndSend(
            appProperties.rabbitmq().exchangeConsultas(), 
            appProperties.rabbitmq().routingKeyNotificacoes(), 
            evento
        );
    }
}
