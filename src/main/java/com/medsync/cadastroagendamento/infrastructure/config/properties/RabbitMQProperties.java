package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public class RabbitMQProperties {
    
    private String exchangeConsultas = "ex_consultas";
    private String exchangeNotificacoes = "ex_notificacoes";
    private String queueHistorico = "q_historico_consultas";
    private String queueNotificacoes = "q_notificacoes_consultas";
    private String queueNotificacoesCliente = "q_notificacoes_cliente";
    private String routingKeyHistorico = "consulta.historico";
    private String routingKeyNotificacoes = "consulta.notificacao";
    private String routingKeyNotificacoesCliente = "notificacao.cliente.*";
    
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
    
    public String getExchangeNotificacoes() {
        return exchangeNotificacoes;
    }
    
    public void setExchangeNotificacoes(String exchangeNotificacoes) {
        this.exchangeNotificacoes = exchangeNotificacoes;
    }
    
    public String getQueueNotificacoesCliente() {
        return queueNotificacoesCliente;
    }
    
    public void setQueueNotificacoesCliente(String queueNotificacoesCliente) {
        this.queueNotificacoesCliente = queueNotificacoesCliente;
    }
    
    public String getRoutingKeyNotificacoesCliente() {
        return routingKeyNotificacoesCliente;
    }
    
    public void setRoutingKeyNotificacoesCliente(String routingKeyNotificacoesCliente) {
        this.routingKeyNotificacoesCliente = routingKeyNotificacoesCliente;
    }
}
