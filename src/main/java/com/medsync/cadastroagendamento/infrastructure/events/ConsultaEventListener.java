package com.medsync.cadastroagendamento.infrastructure.events;

import com.medsync.cadastroagendamento.domain.events.ConsultaCriadaEvent;
import com.medsync.cadastroagendamento.domain.events.ConsultaEditadaEvent;
import com.medsync.cadastroagendamento.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConsultaEventListener {
    
    private final RabbitTemplate rabbitTemplate;
    
    public ConsultaEventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @RabbitListener(queues = RabbitMQConfig.FILA_HISTORICO)
    public void handleConsultaCriadaHistorico(ConsultaCriadaEvent event) {
        // Processar evento para histórico
        Map<String, Object> eventoHistorico = new HashMap<>();
        eventoHistorico.put("evento", "consulta_criada_historico");
        eventoHistorico.put("consulta_id", event.getConsultaId());
        eventoHistorico.put("paciente_id", event.getPacienteId());
        eventoHistorico.put("medico_id", event.getMedicoId());
        eventoHistorico.put("criado_por_id", event.getCriadoPorId());
        eventoHistorico.put("data_hora", event.getDataHora());
        eventoHistorico.put("timestamp", event.getTimestamp());
        
        // Enviar para o serviço de histórico
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CONSULTAS, 
                                     "consulta.criada.historico", eventoHistorico);
    }
    
    @RabbitListener(queues = RabbitMQConfig.FILA_NOTIFICACOES)
    public void handleConsultaCriadaNotificacao(ConsultaCriadaEvent event) {
        // Processar evento para notificações
        Map<String, Object> eventoNotificacao = new HashMap<>();
        eventoNotificacao.put("evento", "consulta_criada_notificacao");
        eventoNotificacao.put("consulta_id", event.getConsultaId());
        eventoNotificacao.put("paciente_id", event.getPacienteId());
        eventoNotificacao.put("medico_id", event.getMedicoId());
        eventoNotificacao.put("data_hora", event.getDataHora());
        
        // Enviar para o serviço de notificações
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CONSULTAS, 
                                     "consulta.criada.notificacao", eventoNotificacao);
    }
    
    public void handleConsultaEditadaHistorico(ConsultaEditadaEvent event) {
        // Processar evento de edição para histórico
        Map<String, Object> eventoHistorico = new HashMap<>();
        eventoHistorico.put("evento", "consulta_editada_historico");
        eventoHistorico.put("consulta_id", event.getConsultaId());
        eventoHistorico.put("paciente_id", event.getPacienteId());
        eventoHistorico.put("medico_id", event.getMedicoId());
        eventoHistorico.put("editado_por_id", event.getEditadoPorId());
        eventoHistorico.put("alteracoes", event.getAlteracoes());
        eventoHistorico.put("timestamp", event.getTimestamp());
        
        // Enviar para o serviço de histórico
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CONSULTAS, 
                                     "consulta.editada.historico", eventoHistorico);
    }
    
    public void handleConsultaEditadaNotificacao(ConsultaEditadaEvent event) {
        // Processar evento de edição para notificações
        Map<String, Object> eventoNotificacao = new HashMap<>();
        eventoNotificacao.put("evento", "consulta_editada_notificacao");
        eventoNotificacao.put("consulta_id", event.getConsultaId());
        eventoNotificacao.put("paciente_id", event.getPacienteId());
        eventoNotificacao.put("medico_id", event.getMedicoId());
        eventoNotificacao.put("alteracoes", event.getAlteracoes());
        
        // Enviar para o serviço de notificações
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CONSULTAS, 
                                     "consulta.editada.notificacao", eventoNotificacao);
    }
}
