package com.medsync.cadastroagendamento.infrastructure.events;

import com.medsync.cadastroagendamento.domain.events.ConsultaCriadaEvent;
import com.medsync.cadastroagendamento.domain.events.ConsultaEditadaEvent;
import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConsultaEventListener {
    
    private final RabbitTemplate rabbitTemplate;
    private final AppProperties appProperties;
    
    public ConsultaEventListener(RabbitTemplate rabbitTemplate, AppProperties appProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.appProperties = appProperties;
    }
    
    @RabbitListener(queues = "q_historico_consultas")
    public void handleConsultaCriadaHistorico(ConsultaCriadaEvent event) {
        Map<String, Object> eventoHistorico = new HashMap<>();
        eventoHistorico.put("evento", "consulta_criada_historico");
        eventoHistorico.put("consulta_id", event.getConsultaId());
        eventoHistorico.put("paciente_id", event.getPacienteId());
        eventoHistorico.put("medico_id", event.getMedicoId());
        eventoHistorico.put("criado_por_id", event.getCriadoPorId());
        eventoHistorico.put("data_hora", event.getDataHora());
        eventoHistorico.put("timestamp", event.getTimestamp());
        
        rabbitTemplate.convertAndSend(appProperties.rabbitmq().exchangeConsultas(), 
                                     appProperties.rabbitmq().routingKeyHistorico(), eventoHistorico);
    }
    
    @RabbitListener(queues = "q_notificacoes_consultas")
    public void handleConsultaCriadaNotificacao(ConsultaCriadaEvent event) {
        Map<String, Object> eventoNotificacao = new HashMap<>();
        eventoNotificacao.put("evento", "consulta_criada_notificacao");
        eventoNotificacao.put("consulta_id", event.getConsultaId());
        eventoNotificacao.put("paciente_id", event.getPacienteId());
        eventoNotificacao.put("medico_id", event.getMedicoId());
        eventoNotificacao.put("data_hora", event.getDataHora());
        
        rabbitTemplate.convertAndSend(appProperties.rabbitmq().exchangeConsultas(), 
                                     appProperties.rabbitmq().routingKeyNotificacoes(), eventoNotificacao);
    }
    
    public void handleConsultaEditadaHistorico(ConsultaEditadaEvent event) {
        Map<String, Object> eventoHistorico = new HashMap<>();
        eventoHistorico.put("evento", "consulta_editada_historico");
        eventoHistorico.put("consulta_id", event.getConsultaId());
        eventoHistorico.put("paciente_id", event.getPacienteId());
        eventoHistorico.put("medico_id", event.getMedicoId());
        eventoHistorico.put("editado_por_id", event.getEditadoPorId());
        eventoHistorico.put("alteracoes", event.getAlteracoes());
        eventoHistorico.put("timestamp", event.getTimestamp());
        
        rabbitTemplate.convertAndSend(appProperties.rabbitmq().exchangeConsultas(), 
                                     appProperties.rabbitmq().routingKeyHistorico(), eventoHistorico);
    }
    
    public void handleConsultaEditadaNotificacao(ConsultaEditadaEvent event) {
        Map<String, Object> eventoNotificacao = new HashMap<>();
        eventoNotificacao.put("evento", "consulta_editada_notificacao");
        eventoNotificacao.put("consulta_id", event.getConsultaId());
        eventoNotificacao.put("paciente_id", event.getPacienteId());
        eventoNotificacao.put("medico_id", event.getMedicoId());
        eventoNotificacao.put("alteracoes", event.getAlteracoes());
        
        rabbitTemplate.convertAndSend(appProperties.rabbitmq().exchangeConsultas(), 
                                     appProperties.rabbitmq().routingKeyNotificacoes(), eventoNotificacao);
    }
}
