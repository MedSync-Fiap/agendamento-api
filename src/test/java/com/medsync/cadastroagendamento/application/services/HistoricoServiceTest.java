package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.application.usecases.BuscarHistoricoPacienteUseCase;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistoricoService Tests")
class HistoricoServiceTest {

    @Mock
    private BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase;

    @InjectMocks
    private HistoricoService historicoService;

    private UUID pacienteId;
    private UUID usuarioLogadoId;
    private HistoricoPacienteResponse historicoResponse;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
        usuarioLogadoId = UUID.randomUUID();
        
        historicoResponse = new HistoricoPacienteResponse(
            pacienteId,
            "Paciente Teste",
            "12345678901",
            "paciente@test.com",
            List.of()
        );
    }

    @Test
    @DisplayName("Deve buscar histórico do paciente com sucesso")
    void deveBuscarHistoricoDoPacienteComSucesso() {
        // Given
        when(buscarHistoricoPacienteUseCase.executar(pacienteId, usuarioLogadoId))
            .thenReturn(historicoResponse);

        // When
        HistoricoPacienteResponse resultado = historicoService
            .buscarHistoricoPaciente(pacienteId, usuarioLogadoId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.pacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.pacienteNome()).isEqualTo("Paciente Teste");
        assertThat(resultado.pacienteCpf()).isEqualTo("12345678901");
        assertThat(resultado.pacienteEmail()).isEqualTo("paciente@test.com");
        assertThat(resultado.consultas()).isEmpty();

        verify(buscarHistoricoPacienteUseCase).executar(pacienteId, usuarioLogadoId);
    }

    @Test
    @DisplayName("Deve chamar use case com parâmetros corretos")
    void deveChamarUseCaseComParametrosCorretos() {
        // Given
        when(buscarHistoricoPacienteUseCase.executar(any(UUID.class), any(UUID.class)))
            .thenReturn(historicoResponse);

        // When
        historicoService.buscarHistoricoPaciente(pacienteId, usuarioLogadoId);

        // Then
        verify(buscarHistoricoPacienteUseCase).executar(eq(pacienteId), eq(usuarioLogadoId));
        verify(buscarHistoricoPacienteUseCase, never()).executar(eq(usuarioLogadoId), eq(pacienteId));
    }

    @Test
    @DisplayName("Deve retornar histórico com consultas quando disponível")
    void deveRetornarHistoricoComConsultasQuandoDisponivel() {
        // Given
        HistoricoPacienteResponse historicoComConsultas = new HistoricoPacienteResponse(
            pacienteId,
            "Paciente Teste",
            "12345678901",
            "paciente@test.com",
            List.of(
                new HistoricoPacienteResponse.ConsultaHistorico(
                    UUID.randomUUID(),
                    new HistoricoPacienteResponse.MedicoInfo(UUID.randomUUID(), "Dr. Silva", "Cardiologia"),
                    new HistoricoPacienteResponse.UsuarioInfo(UUID.randomUUID(), "Admin"),
                    java.time.LocalDateTime.now(),
                    "AGENDADA",
                    "Consulta de rotina",
                    List.of()
                )
            )
        );

        when(buscarHistoricoPacienteUseCase.executar(pacienteId, usuarioLogadoId))
            .thenReturn(historicoComConsultas);

        // When
        HistoricoPacienteResponse resultado = historicoService
            .buscarHistoricoPaciente(pacienteId, usuarioLogadoId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.consultas()).hasSize(1);
        assertThat(resultado.consultas().get(0).medico().nome()).isEqualTo("Dr. Silva");
        assertThat(resultado.consultas().get(0).medico().especialidade()).isEqualTo("Cardiologia");

        verify(buscarHistoricoPacienteUseCase).executar(pacienteId, usuarioLogadoId);
    }

    @Test
    @DisplayName("Deve propagar exceção do use case")
    void devePropagarExcecaoDoUseCase() {
        // Given
        RuntimeException exception = new RuntimeException("Erro no use case");
        when(buscarHistoricoPacienteUseCase.executar(pacienteId, usuarioLogadoId))
            .thenThrow(exception);

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> 
            historicoService.buscarHistoricoPaciente(pacienteId, usuarioLogadoId)
        );

        verify(buscarHistoricoPacienteUseCase).executar(pacienteId, usuarioLogadoId);
    }

    @Test
    @DisplayName("Deve retornar histórico vazio quando use case retorna vazio")
    void deveRetornarHistoricoVazioQuandoUseCaseRetornaVazio() {
        // Given
        HistoricoPacienteResponse historicoVazio = new HistoricoPacienteResponse(
            pacienteId,
            "Paciente não encontrado",
            "",
            "",
            List.of()
        );

        when(buscarHistoricoPacienteUseCase.executar(pacienteId, usuarioLogadoId))
            .thenReturn(historicoVazio);

        // When
        HistoricoPacienteResponse resultado = historicoService
            .buscarHistoricoPaciente(pacienteId, usuarioLogadoId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.pacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.pacienteNome()).isEqualTo("Paciente não encontrado");
        assertThat(resultado.pacienteCpf()).isEmpty();
        assertThat(resultado.pacienteEmail()).isEmpty();
        assertThat(resultado.consultas()).isEmpty();

        verify(buscarHistoricoPacienteUseCase).executar(pacienteId, usuarioLogadoId);
    }
}