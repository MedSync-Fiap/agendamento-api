package com.medsync.cadastroagendamento.infrastructure.clients;

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
@DisplayName("HistoricoFeignClientFallback Tests")
class HistoricoFeignClientFallbackTest {

    @InjectMocks
    private HistoricoFeignClientFallback fallback;

    private UUID pacienteId;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve retornar histórico vazio quando serviço está indisponível")
    void deveRetornarHistoricoVazioQuandoServicoEstaIndisponivel() {
        // When
        HistoricoPacienteResponse response = fallback.buscarHistoricoPaciente(pacienteId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.pacienteId()).isEqualTo(pacienteId);
        assertThat(response.pacienteNome()).isEqualTo("Serviço de histórico indisponível");
        assertThat(response.pacienteCpf()).isEmpty();
        assertThat(response.pacienteEmail()).isEmpty();
        assertThat(response.consultas()).isEmpty();
    }

    @Test
    @DisplayName("Deve implementar interface HistoricoFeignClient")
    void deveImplementarInterfaceHistoricoFeignClient() {
        // Then
        assertThat(fallback).isInstanceOf(HistoricoFeignClient.class);
    }

    @Test
    @DisplayName("Deve ter método buscarHistoricoPaciente")
    void deveTerMetodoBuscarHistoricoPaciente() {
        // When
        HistoricoPacienteResponse response = fallback.buscarHistoricoPaciente(pacienteId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.pacienteId()).isEqualTo(pacienteId);
    }

    @Test
    @DisplayName("Deve retornar resposta consistente para diferentes IDs")
    void deveRetornarRespostaConsistenteParaDiferentesIds() {
        // Given
        UUID outroPacienteId = UUID.randomUUID();

        // When
        HistoricoPacienteResponse response1 = fallback.buscarHistoricoPaciente(pacienteId);
        HistoricoPacienteResponse response2 = fallback.buscarHistoricoPaciente(outroPacienteId);

        // Then
        assertThat(response1.pacienteId()).isEqualTo(pacienteId);
        assertThat(response2.pacienteId()).isEqualTo(outroPacienteId);
        assertThat(response1.pacienteNome()).isEqualTo(response2.pacienteNome());
        assertThat(response1.consultas()).isEmpty();
        assertThat(response2.consultas()).isEmpty();
    }
}
