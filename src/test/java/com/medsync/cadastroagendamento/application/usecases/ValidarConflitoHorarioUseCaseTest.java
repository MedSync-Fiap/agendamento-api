package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.ConflitoHorarioException;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidarConflitoHorarioUseCase Tests")
class ValidarConflitoHorarioUseCaseTest {

    @Mock
    private ConsultaGateway consultaGateway;

    @InjectMocks
    private ValidarConflitoHorarioUseCase validarConflitoHorarioUseCase;

    private UUID medicoId;
    private LocalDateTime dataHora;
    private UUID consultaId;

    @BeforeEach
    void setUp() {
        medicoId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        consultaId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve validar horário sem conflito com sucesso")
    void deveValidarHorarioSemConflitoComSucesso() {
        // Given
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(false);

        // When & Then
        assertThatCode(() -> validarConflitoHorarioUseCase.executar(medicoId, dataHora))
                .doesNotThrowAnyException();

        verify(consultaGateway).existeConsultaNoHorario(medicoId, dataHora);
    }

    @Test
    @DisplayName("Deve lançar ConflitoHorarioException quando há conflito de horário")
    void deveLancarConflitoHorarioExceptionQuandoHaConflitoHorario() {
        // Given
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> validarConflitoHorarioUseCase.executar(medicoId, dataHora))
                .isInstanceOf(ConflitoHorarioException.class)
                .hasMessage("Já existe uma consulta agendada para o médico " + medicoId + " no horário " + dataHora);

        verify(consultaGateway).existeConsultaNoHorario(medicoId, dataHora);
    }

    @Test
    @DisplayName("Deve validar horário sem conflito excluindo consulta específica com sucesso")
    void deveValidarHorarioSemConflitoExcluindoConsultaEspecificaComSucesso() {
        // Given
        when(consultaGateway.existeConsultaNoHorarioExcluindo(medicoId, dataHora, consultaId)).thenReturn(false);

        // When & Then
        assertThatCode(() -> validarConflitoHorarioUseCase.executarExcluindo(medicoId, dataHora, consultaId))
                .doesNotThrowAnyException();

        verify(consultaGateway).existeConsultaNoHorarioExcluindo(medicoId, dataHora, consultaId);
    }

    @Test
    @DisplayName("Deve lançar ConflitoHorarioException quando há conflito excluindo consulta específica")
    void deveLancarConflitoHorarioExceptionQuandoHaConflitoExcluindoConsultaEspecifica() {
        // Given
        when(consultaGateway.existeConsultaNoHorarioExcluindo(medicoId, dataHora, consultaId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> validarConflitoHorarioUseCase.executarExcluindo(medicoId, dataHora, consultaId))
                .isInstanceOf(ConflitoHorarioException.class)
                .hasMessage("Já existe uma consulta agendada para o médico " + medicoId + " no horário " + dataHora);

        verify(consultaGateway).existeConsultaNoHorarioExcluindo(medicoId, dataHora, consultaId);
    }
}
