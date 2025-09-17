package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public class RabbitMQProperties {
    
    private String exchangeConsultas = "ex_consultas";
    private String queueHistorico = "q_historico_consultas";
    private String queueNotificacoes = "q_notificacoes_consultas";
    private String routingKeyHistorico = "consulta.historico";
    private String routingKeyNotificacoes = "consulta.notificacao";
    
    public RabbitMQProperties() {
    }
    
    public String getExchangeConsultas() {
        return exchangeConsultas;
    }
    
    public void setExchangeConsultas(String exchangeConsultas) {
        this.exchangeConsultas = exchangeConsultas;
    }
    
    public String getQueueHistorico() {
        return queueHistorico;
    }
    
    public void setQueueHistorico(String queueHistorico) {
        this.queueHistorico = queueHistorico;
    }
    
    public String getQueueNotificacoes() {
        return queueNotificacoes;
    }
    
    public void setQueueNotificacoes(String queueNotificacoes) {
        this.queueNotificacoes = queueNotificacoes;
    }
    
    public String getRoutingKeyHistorico() {
        return routingKeyHistorico;
    }
    
    public void setRoutingKeyHistorico(String routingKeyHistorico) {
        this.routingKeyHistorico = routingKeyHistorico;
    }
    
    public String getRoutingKeyNotificacoes() {
        return routingKeyNotificacoes;
    }
    
    public void setRoutingKeyNotificacoes(String routingKeyNotificacoes) {
        this.routingKeyNotificacoes = routingKeyNotificacoes;
    }
}
