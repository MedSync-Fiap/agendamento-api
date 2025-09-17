package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.BuscarHistoricoPacienteUseCase;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HistoricoController.class)
class HistoricoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase;

    private UUID pacienteId;
    private UUID medicoId;
    private HistoricoPacienteResponse historicoResponse;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.fromString("850e8400-e29b-41d4-a716-446655440004");
        medicoId = UUID.fromString("850e8400-e29b-41d4-a716-446655440002");
        
        historicoResponse = new HistoricoPacienteResponse(
            pacienteId,
            "Paciente Ana Costa",
            "12345678904",
            "ana.costa@medsync.com",
            List.of()
        );
    }

    @Test
    @DisplayName("Deve buscar histórico de paciente com sucesso")
    void deveBuscarHistoricoPacienteComSucesso() throws Exception {
        // Given
        when(buscarHistoricoPacienteUseCase.executar(eq(pacienteId), eq(medicoId)))
                .thenReturn(historicoResponse);

        // When & Then
        mockMvc.perform(get("/historico/paciente/{pacienteId}", pacienteId)
                .header("X-User-Id", medicoId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.pacienteId").value(pacienteId.toString()))
                .andExpect(jsonPath("$.pacienteNome").value("Paciente Ana Costa"))
                .andExpect(jsonPath("$.pacienteCpf").value("12345678904"))
                .andExpect(jsonPath("$.pacienteEmail").value("ana.costa@medsync.com"))
                .andExpect(jsonPath("$.consultas").isArray());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando header X-User-Id não é fornecido")
    void deveRetornarErro400QuandoHeaderXUserIdNaoEFornecido() throws Exception {
        // When & Then
        mockMvc.perform(get("/historico/paciente/{pacienteId}", pacienteId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando X-User-Id é inválido")
    void deveRetornarErro400QuandoXUserIdEInvalido() throws Exception {
        // When & Then
        mockMvc.perform(get("/historico/paciente/{pacienteId}", pacienteId)
                .header("X-User-Id", "uuid-invalido"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando pacienteId é inválido")
    void deveRetornarErro400QuandoPacienteIdEInvalido() throws Exception {
        // When & Then
        mockMvc.perform(get("/historico/paciente/{pacienteId}", "uuid-invalido")
                .header("X-User-Id", medicoId.toString()))
                .andExpect(status().isBadRequest());
    }
}

