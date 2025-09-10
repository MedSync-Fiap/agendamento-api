package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMQProperties(
        String exchangeConsultas,
        String queueHistorico,
        String queueNotificacoes,
        String routingKeyHistorico,
        String routingKeyNotificacoes
) {
    
    public RabbitMQProperties() {
        this("ex_consultas", "q_historico_consultas", "q_notificacoes_consultas", "consulta.historico", "consulta.notificacao");
    }
}
