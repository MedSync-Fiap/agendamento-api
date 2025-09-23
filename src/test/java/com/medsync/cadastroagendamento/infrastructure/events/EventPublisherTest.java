package com.medsync.cadastroagendamento.infrastructure.events;

import com.medsync.cadastroagendamento.domain.events.ConsultaHistoricoEvent;
import com.medsync.cadastroagendamento.domain.events.ConsultaNotificacaoEvent;
import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import com.medsync.cadastroagendamento.infrastructure.config.properties.RabbitMQProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventPublisher Tests")
class EventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AppProperties appProperties;

    @Mock
    private RabbitMQProperties rabbitMQProperties;

    @InjectMocks
    private EventPublisher eventPublisher;

    private ConsultaNotificacaoEvent notificacaoEvent;
    private ConsultaHistoricoEvent historicoEvent;

    @BeforeEach
    void setUp() {
        UUID consultaId = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        UUID criadoPorId = UUID.randomUUID();
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        LocalDateTime timestamp = LocalDateTime.now();

        notificacaoEvent = new ConsultaNotificacaoEvent(
            consultaId, pacienteId, medicoId, criadoPorId, dataHora, 
            "AGENDADA", "Consulta de teste", "CRIADA", timestamp
        );

        historicoEvent = new ConsultaHistoricoEvent(
            consultaId, dataHora, "AGENDADA", "Consulta de teste", "CRIADA", timestamp,
            pacienteId, "Paciente Teste", "12345678901", "paciente@test.com", 
            java.time.LocalDate.now().minusYears(30),
            medicoId, "Médico Teste", "98765432109", "medico@test.com", "Cardiologia",
            criadoPorId, "Admin Teste", "admin@test.com", "ADMIN"
        );

        when(appProperties.getRabbitmq()).thenReturn(rabbitMQProperties);
        when(rabbitMQProperties.getExchangeConsultas()).thenReturn("ex_consultas");
        when(rabbitMQProperties.getRoutingKeyNotificacoes()).thenReturn("consulta.notificacao");
        when(rabbitMQProperties.getRoutingKeyHistorico()).thenReturn("consulta.historico");
    }

    @Test
    @DisplayName("Deve publicar evento de notificação com sucesso")
    void devePublicarEventoNotificacaoComSucesso() {
        // When
        eventPublisher.publishNotificacaoConsulta(notificacaoEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
            eq("ex_consultas"),
            eq("consulta.notificacao"),
            eq(notificacaoEvent)
        );
    }

    @Test
    @DisplayName("Deve publicar evento de histórico com sucesso")
    void devePublicarEventoHistoricoComSucesso() {
        // When
        eventPublisher.publishHistoricoConsulta(historicoEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
            eq("ex_consultas"),
            eq("consulta.historico"),
            eq(historicoEvent)
        );
    }

    @Test
    @DisplayName("Deve tratar erro ao publicar evento de notificação")
    void deveTratarErroAoPublicarEventoNotificacao() {
        // Given
        doThrow(new RuntimeException("Erro de conexão")).when(rabbitTemplate)
            .convertAndSend(eq("ex_consultas"), eq("consulta.notificacao"), any(ConsultaNotificacaoEvent.class));

        // When & Then
        eventPublisher.publishNotificacaoConsulta(notificacaoEvent);
        
        // Verifica que não lança exceção (tratamento interno)
        verify(rabbitTemplate).convertAndSend(
            eq("ex_consultas"),
            eq("consulta.notificacao"),
            eq(notificacaoEvent)
        );
    }

    @Test
    @DisplayName("Deve tratar erro ao publicar evento de histórico")
    void deveTratarErroAoPublicarEventoHistorico() {
        // Given
        doThrow(new RuntimeException("Erro de conexão")).when(rabbitTemplate)
            .convertAndSend(eq("ex_consultas"), eq("consulta.historico"), any(ConsultaHistoricoEvent.class));

        // When & Then
        eventPublisher.publishHistoricoConsulta(historicoEvent);
        
        // Verifica que não lança exceção (tratamento interno)
        verify(rabbitTemplate).convertAndSend(
            eq("ex_consultas"),
            eq("consulta.historico"),
            eq(historicoEvent)
        );
    }

    @Test
    @DisplayName("Deve publicar eventos com dados corretos")
    void devePublicarEventosComDadosCorretos() {
        // When
        eventPublisher.publishNotificacaoConsulta(notificacaoEvent);
        eventPublisher.publishHistoricoConsulta(historicoEvent);

        // Then
        verify(rabbitTemplate, times(2)).convertAndSend(
            eq("ex_consultas"),
            any(String.class),
            any(Object.class)
        );
    }
}
