package com.medsync.cadastroagendamento.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    public static final String EXCHANGE_CONSULTAS = "ex_consultas";
    public static final String FILA_HISTORICO = "fila_historico";
    public static final String FILA_NOTIFICACOES = "fila_notificacoes";
    
    @Bean
    public TopicExchange exchangeConsultas() {
        return new TopicExchange(EXCHANGE_CONSULTAS);
    }
    
    @Bean
    public Queue filaHistorico() {
        return QueueBuilder.durable(FILA_HISTORICO).build();
    }
    
    @Bean
    public Queue filaNotificacoes() {
        return QueueBuilder.durable(FILA_NOTIFICACOES).build();
    }
    
    @Bean
    public Binding bindingHistorico() {
        return BindingBuilder
                .bind(filaHistorico())
                .to(exchangeConsultas())
                .with("consulta.*.historico");
    }
    
    @Bean
    public Binding bindingNotificacoes() {
        return BindingBuilder
                .bind(filaNotificacoes())
                .to(exchangeConsultas())
                .with("consulta.*.notificacao");
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
