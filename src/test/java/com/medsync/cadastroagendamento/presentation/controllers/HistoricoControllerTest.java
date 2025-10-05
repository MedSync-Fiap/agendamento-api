package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.BuscarHistoricoPacienteUseCase;
import com.medsync.cadastroagendamento.application.usecases.BuscarHistoricoGraphQLUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HistoricoController Tests")
class HistoricoControllerTest {

    @Mock
    private BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase;

    @Mock
    private BuscarHistoricoGraphQLUseCase buscarHistoricoGraphQLUseCase;

    @InjectMocks
    private HistoricoController historicoController;

    private UUID pacienteId;
    private UUID consultaId;
    private Map<String, Object> historicoResponse;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
        consultaId = UUID.randomUUID();
        
        historicoResponse = Map.of(
            "patient", Map.of(
                "id", pacienteId.toString(),
                "name", "Paciente Teste",
                "cpf", "12345678901",
                "email", "paciente@test.com"
            ),
            "appointments", List.of()
        );
    }

    @Test
    @DisplayName("Deve buscar histórico do paciente com sucesso")
    void deveBuscarHistoricoDoPacienteComSucesso() {
        // Given
        when(buscarHistoricoPacienteUseCase.executar(pacienteId))
            .thenReturn(historicoResponse);

        // When
        ResponseEntity<Map<String, Object>> response = historicoController
            .buscarHistoricoPaciente(pacienteId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("patient");
        assertThat(response.getBody()).containsKey("appointments");

        verify(buscarHistoricoPacienteUseCase).executar(pacienteId);
    }

    @Test
    @DisplayName("Deve buscar consulta específica com sucesso")
    void deveBuscarConsultaEspecificaComSucesso() {
        // Given
        Map<String, Object> consultaResponse = Map.of(
            "id", consultaId.toString(),
            "appointmentDateTime", "2024-12-15T10:00:00",
            "status", "AGENDADA",
            "doctor", Map.of(
                "id", UUID.randomUUID().toString(),
                "name", "Dr. Silva",
                "specialty", "Cardiologia"
            )
        );

        when(buscarHistoricoGraphQLUseCase.buscarHistoricoConsulta(consultaId, pacienteId))
            .thenReturn(consultaResponse);

        // When
        ResponseEntity<Map<String, Object>> response = historicoController
            .buscarHistoricoConsulta(consultaId, pacienteId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsKey("id");
        assertThat(response.getBody()).containsKey("appointmentDateTime");

        verify(buscarHistoricoGraphQLUseCase).buscarHistoricoConsulta(consultaId, pacienteId);
    }
}