package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class HistoricoServiceTest {

    @InjectMocks
    private HistoricoService historicoService;

    private UUID pacienteId;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.fromString("850e8400-e29b-41d4-a716-446655440004");
    }

    @Test
    @DisplayName("Deve buscar histórico de paciente e retornar dados mockados")
    void deveBuscarHistoricoPacienteERetornarDadosMockados() {
        // When
        HistoricoPacienteResponse response = historicoService.buscarHistoricoPaciente(pacienteId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.pacienteId()).isEqualTo(pacienteId);
        assertThat(response.pacienteNome()).isEqualTo("Paciente Mock");
        assertThat(response.pacienteCpf()).isEqualTo("12345678901");
        assertThat(response.pacienteEmail()).isEqualTo("paciente@email.com");
        assertThat(response.consultas()).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar resposta consistente para diferentes IDs de paciente")
    void deveRetornarRespostaConsistenteParaDiferentesIdsDePaciente() {
        // Given
        UUID outroPacienteId = UUID.fromString("850e8400-e29b-41d4-a716-446655440005");

        // When
        HistoricoPacienteResponse response1 = historicoService.buscarHistoricoPaciente(pacienteId);
        HistoricoPacienteResponse response2 = historicoService.buscarHistoricoPaciente(outroPacienteId);

        // Then
        assertThat(response1.pacienteId()).isEqualTo(pacienteId);
        assertThat(response2.pacienteId()).isEqualTo(outroPacienteId);
        assertThat(response1.pacienteNome()).isEqualTo(response2.pacienteNome());
        assertThat(response1.pacienteCpf()).isEqualTo(response2.pacienteCpf());
        assertThat(response1.pacienteEmail()).isEqualTo(response2.pacienteEmail());
    }
}

