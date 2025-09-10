package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.events.ConsultaCriadaEvent;
import com.medsync.cadastroagendamento.domain.events.ConsultaEditadaEvent;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublicarEventoNotificacaoUseCase Tests")
class PublicarEventoNotificacaoUseCaseTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private PublicarEventoNotificacaoUseCase publicarEventoNotificacaoUseCase;

    private ConsultaCriadaEvent consultaCriadaEvent;
    private ConsultaEditadaEvent consultaEditadaEvent;

    @BeforeEach
    void setUp() {
        UUID consultaId = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        UUID criadoPorId = UUID.randomUUID();
        LocalDateTime dataHora = LocalDateTime.now().plusDays(1);
        LocalDateTime timestamp = LocalDateTime.now();

        consultaCriadaEvent = new ConsultaCriadaEvent(
                consultaId, pacienteId, medicoId, criadoPorId, dataHora, timestamp
        );

        Map<String, Object> alteracoes = new HashMap<>();
        alteracoes.put("medicoId", UUID.randomUUID());
        alteracoes.put("dataHora", LocalDateTime.now().plusDays(2));

        consultaEditadaEvent = new ConsultaEditadaEvent(
                consultaId, pacienteId, medicoId, criadoPorId, alteracoes, timestamp
        );

        // Mock AppProperties
        when(appProperties.rabbitmq()).thenReturn(new RabbitMQProperties());
    }

    @Test
    @DisplayName("Deve publicar evento de consulta criada com sucesso")
    void devePublicarEventoConsultaCriadaComSucesso() {
        // When
        publicarEventoNotificacaoUseCase.executar(consultaCriadaEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("ex_consultas"),
                eq("consulta.notificacao"),
                eq(consultaCriadaEvent)
        );
    }

    @Test
    @DisplayName("Deve publicar evento de consulta editada com sucesso")
    void devePublicarEventoConsultaEditadaComSucesso() {
        // When
        publicarEventoNotificacaoUseCase.executar(consultaEditadaEvent);

        // Then
        verify(rabbitTemplate).convertAndSend(
                eq("ex_consultas"),
                eq("consulta.notificacao"),
                eq(consultaEditadaEvent)
        );
    }
}
