package com.medsync.cadastroagendamento.infrastructure.events;

import com.medsync.cadastroagendamento.domain.events.ConsultaHistoricoEvent;
import com.medsync.cadastroagendamento.domain.events.ConsultaNotificacaoEvent;
import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import com.medsync.cadastroagendamento.infrastructure.events.dto.NotificacaoConsultaPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    private final AppProperties appProperties;
    
    public EventPublisher(RabbitTemplate rabbitTemplate, AppProperties appProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.appProperties = appProperties;
    }
    
    public void publishNotificacaoConsulta(ConsultaNotificacaoEvent event) {
        try {
            logger.info("Publicando evento de notificação de consulta: {}", event.consultaId());
            
            NotificacaoConsultaPayload payload = new NotificacaoConsultaPayload(
                event.consultaId(),
                event.pacienteId(),
                event.medicoId(),
                event.criadoPorId(),
                event.dataHora(),
                event.status(),
                event.observacoes(),
                event.tipoEvento(),
                event.timestamp()
            );
            
            rabbitTemplate.convertAndSend(
                appProperties.getRabbitmq().getExchangeConsultas(),
                appProperties.getRabbitmq().getRoutingKeyNotificacoes(),
                payload
            );
            
            logger.info("Evento de notificação publicado com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao publicar evento de notificação", e);
        }
    }
    
    public void publishHistoricoConsulta(ConsultaHistoricoEvent event) {
        try {
            logger.info("Publicando evento de histórico de consulta: {}", event.consultaId());
            
            rabbitTemplate.convertAndSend(
                appProperties.getRabbitmq().getExchangeConsultas(),
                appProperties.getRabbitmq().getRoutingKeyHistorico(),
                event
            );
            
            logger.info("Evento de histórico publicado com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao publicar evento de histórico", e);
        }
    }
}
