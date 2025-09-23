package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.services.HistoricoService;
import com.medsync.cadastroagendamento.infrastructure.security.SecurityUtils;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistoricoController Tests")
class HistoricoControllerTest {

    @Mock
    private HistoricoService historicoService;

    @InjectMocks
    private HistoricoController historicoController;

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
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(usuarioLogadoId);
            
            when(historicoService.buscarHistoricoPaciente(pacienteId, usuarioLogadoId))
                .thenReturn(historicoResponse);

            // When
            ResponseEntity<HistoricoPacienteResponse> response = historicoController
                .buscarHistoricoPaciente(pacienteId);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().pacienteId()).isEqualTo(pacienteId);
            assertThat(response.getBody().pacienteNome()).isEqualTo("Paciente Teste");
            assertThat(response.getBody().pacienteCpf()).isEqualTo("12345678901");
            assertThat(response.getBody().pacienteEmail()).isEqualTo("paciente@test.com");
            assertThat(response.getBody().consultas()).isEmpty();

            verify(historicoService).buscarHistoricoPaciente(pacienteId, usuarioLogadoId);
        }
    }

    @Test
    @DisplayName("Deve chamar service com parâmetros corretos")
    void deveChamarServiceComParametrosCorretos() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(usuarioLogadoId);
            
            when(historicoService.buscarHistoricoPaciente(any(UUID.class), any(UUID.class)))
                .thenReturn(historicoResponse);

            // When
            historicoController.buscarHistoricoPaciente(pacienteId);

            // Then
            verify(historicoService).buscarHistoricoPaciente(eq(pacienteId), eq(usuarioLogadoId));
            verify(historicoService, never()).buscarHistoricoPaciente(eq(usuarioLogadoId), eq(pacienteId));
        }
    }

    @Test
    @DisplayName("Deve retornar histórico com consultas quando disponível")
    void deveRetornarHistoricoComConsultasQuandoDisponivel() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(usuarioLogadoId);
            
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

            when(historicoService.buscarHistoricoPaciente(pacienteId, usuarioLogadoId))
                .thenReturn(historicoComConsultas);

            // When
            ResponseEntity<HistoricoPacienteResponse> response = historicoController
                .buscarHistoricoPaciente(pacienteId);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().consultas()).hasSize(1);
            assertThat(response.getBody().consultas().get(0).medico().nome()).isEqualTo("Dr. Silva");
            assertThat(response.getBody().consultas().get(0).medico().especialidade()).isEqualTo("Cardiologia");

            verify(historicoService).buscarHistoricoPaciente(pacienteId, usuarioLogadoId);
        }
    }

    @Test
    @DisplayName("Deve obter ID do usuário logado do SecurityUtils")
    void deveObterIdDoUsuarioLogadoDoSecurityUtils() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(usuarioLogadoId);
            
            when(historicoService.buscarHistoricoPaciente(pacienteId, usuarioLogadoId))
                .thenReturn(historicoResponse);

            // When
            historicoController.buscarHistoricoPaciente(pacienteId);

            // Then
            mockedSecurityUtils.verify(SecurityUtils::getCurrentUserId);
            verify(historicoService).buscarHistoricoPaciente(pacienteId, usuarioLogadoId);
        }
    }

    @Test
    @DisplayName("Deve retornar histórico vazio quando service retorna vazio")
    void deveRetornarHistoricoVazioQuandoServiceRetornaVazio() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(usuarioLogadoId);
            
            HistoricoPacienteResponse historicoVazio = new HistoricoPacienteResponse(
                pacienteId,
                "Paciente não encontrado",
                "",
                "",
                List.of()
            );

            when(historicoService.buscarHistoricoPaciente(pacienteId, usuarioLogadoId))
                .thenReturn(historicoVazio);

            // When
            ResponseEntity<HistoricoPacienteResponse> response = historicoController
                .buscarHistoricoPaciente(pacienteId);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().pacienteId()).isEqualTo(pacienteId);
            assertThat(response.getBody().pacienteNome()).isEqualTo("Paciente não encontrado");
            assertThat(response.getBody().pacienteCpf()).isEmpty();
            assertThat(response.getBody().pacienteEmail()).isEmpty();
            assertThat(response.getBody().consultas()).isEmpty();

            verify(historicoService).buscarHistoricoPaciente(pacienteId, usuarioLogadoId);
        }
    }
}