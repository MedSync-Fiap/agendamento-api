package com.medsync.cadastroagendamento.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    private final AppProperties appProperties;
    
    public RabbitMQConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
    
    @Bean
    public TopicExchange exchangeConsultas() {
        return new TopicExchange(appProperties.getRabbitmq().getExchangeConsultas());
    }
    
    @Bean
    public Queue filaHistorico() {
        return QueueBuilder.durable(appProperties.getRabbitmq().getQueueHistorico()).build();
    }
    
    @Bean
    public Queue filaNotificacoes() {
        return QueueBuilder.durable(appProperties.getRabbitmq().getQueueNotificacoes()).build();
    }
    
    @Bean
    public Binding bindingHistorico() {
        return BindingBuilder
                .bind(filaHistorico())
                .to(exchangeConsultas())
                .with(appProperties.getRabbitmq().getRoutingKeyHistorico());
    }
    
    @Bean
    public Binding bindingNotificacoes() {
        return BindingBuilder
                .bind(filaNotificacoes())
                .to(exchangeConsultas())
                .with(appProperties.getRabbitmq().getRoutingKeyNotificacoes());
    }
    
    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setCreateMessageIds(true);
        return converter;
    }
    
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
