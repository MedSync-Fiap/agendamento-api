package com.medsync.cadastroagendamento.infrastructure.config;

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
        return new TopicExchange(appProperties.rabbitmq().exchangeConsultas());
    }
    
    @Bean
    public Queue filaHistorico() {
        return QueueBuilder.durable(appProperties.rabbitmq().queueHistorico()).build();
    }
    
    @Bean
    public Queue filaNotificacoes() {
        return QueueBuilder.durable(appProperties.rabbitmq().queueNotificacoes()).build();
    }
    
    @Bean
    public Binding bindingHistorico() {
        return BindingBuilder
                .bind(filaHistorico())
                .to(exchangeConsultas())
                .with(appProperties.rabbitmq().routingKeyHistorico());
    }
    
    @Bean
    public Binding bindingNotificacoes() {
        return BindingBuilder
                .bind(filaNotificacoes())
                .to(exchangeConsultas())
                .with(appProperties.rabbitmq().routingKeyNotificacoes());
    }
    
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
