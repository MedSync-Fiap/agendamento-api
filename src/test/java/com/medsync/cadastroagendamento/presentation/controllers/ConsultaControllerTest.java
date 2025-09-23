package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.services.ConsultaService;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.infrastructure.security.SecurityUtils;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.mappers.ConsultaDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsultaController.class)
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultaService consultaService;

    @MockBean
    private ConsultaDtoMapper consultaDtoMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private CriarConsultaRequest criarConsultaRequest;
    private Consulta consulta;
    private UUID usuarioLogadoId;

    @BeforeEach
    void setUp() {
        usuarioLogadoId = UUID.fromString("850e8400-e29b-41d4-a716-446655440003");
        
        criarConsultaRequest = new CriarConsultaRequest(
            UUID.fromString("850e8400-e29b-41d4-a716-446655440004"), // pacienteId
            UUID.fromString("850e8400-e29b-41d4-a716-446655440002"), // medicoId
            LocalDateTime.now().plusDays(1), // dataHora
            "Consulta de rotina" // observacoes
        );

        consulta = new Consulta();
        consulta.setId(UUID.randomUUID());
        consulta.setPacienteId(criarConsultaRequest.pacienteId());
        consulta.setMedicoId(criarConsultaRequest.medicoId());
        consulta.setCriadoPorId(usuarioLogadoId);
        consulta.setDataHora(criarConsultaRequest.dataHora());
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setObservacoes(criarConsultaRequest.observacoes());
    }

    @Test
    @DisplayName("Deve criar consulta com sucesso")
    void deveCriarConsultaComSucesso() throws Exception {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(usuarioLogadoId);
            
            // Given
            when(consultaService.criarConsulta(any(), eq(usuarioLogadoId))).thenReturn(consulta);
            when(consultaDtoMapper.toUseCaseRequest(any(CriarConsultaRequest.class))).thenReturn(criarConsultaRequest);
            when(consultaDtoMapper.toResponse(any())).thenReturn(
                new com.medsync.cadastroagendamento.presentation.dto.ConsultaResponse(
                    consulta.getId(),
                    consulta.getPacienteId(),
                    consulta.getMedicoId(),
                    consulta.getCriadoPorId(),
                    consulta.getDataHora(),
                    consulta.getStatus(),
                    consulta.getObservacoes(),
                    consulta.getCriadoEm(),
                    consulta.getAtualizadoEm()
                )
            );

            // When & Then
            mockMvc.perform(post("/consultas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(criarConsultaRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(consulta.getId().toString()))
                    .andExpect(jsonPath("$.pacienteId").value(consulta.getPacienteId().toString()))
                    .andExpect(jsonPath("$.medicoId").value(consulta.getMedicoId().toString()))
                    .andExpect(jsonPath("$.criadoPorId").value(consulta.getCriadoPorId().toString()))
                    .andExpect(jsonPath("$.status").value("AGENDADA"))
                    .andExpect(jsonPath("$.observacoes").value("Consulta de rotina"));
        }
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando dados da consulta são inválidos")
    void deveRetornarErro400QuandoDadosDaConsultaSaoInvalidos() throws Exception {
        // Given
        CriarConsultaRequest requestInvalido = new CriarConsultaRequest(
            null, // pacienteId inválido
            UUID.fromString("850e8400-e29b-41d4-a716-446655440002"),
            LocalDateTime.now().plusDays(1),
            "Consulta de rotina"
        );

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(usuarioLogadoId);
            
            // When & Then
            mockMvc.perform(post("/consultas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestInvalido)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Deve retornar erro 401 quando usuário não está autenticado")
    void deveRetornarErro401QuandoUsuarioNaoEstaAutenticado() throws Exception {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenThrow(new SecurityException("User not authenticated"));
            
            // When & Then
            mockMvc.perform(post("/consultas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(criarConsultaRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando dataHora é no passado")
    void deveRetornarErro400QuandoDataHoraENoPassado() throws Exception {
        // Given
        CriarConsultaRequest requestDataPassado = new CriarConsultaRequest(
            UUID.fromString("850e8400-e29b-41d4-a716-446655440004"),
            UUID.fromString("850e8400-e29b-41d4-a716-446655440002"),
            LocalDateTime.now().minusDays(1), // Data no passado
            "Consulta de rotina"
        );

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(usuarioLogadoId);
            
            // When & Then
            mockMvc.perform(post("/consultas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDataPassado)))
                    .andExpect(status().isBadRequest());
        }
    }
}

