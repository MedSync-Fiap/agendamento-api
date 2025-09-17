package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.services.AuthService;
import com.medsync.cadastroagendamento.presentation.dto.AuthResponse;
import com.medsync.cadastroagendamento.presentation.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("joao.silva@medsync.com", "senha123");
        
        authResponse = new AuthResponse(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "Bearer",
            86400000L,
            LocalDateTime.now().plusSeconds(86400),
            new AuthResponse.UserInfo(
                "850e8400-e29b-41d4-a716-446655440002",
                "Dr. João Silva",
                "joao.silva@medsync.com",
                "MEDICO",
                List.of("CRIAR_CONSULTA", "EDITAR_CONSULTA")
            )
        );
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() throws Exception {
        // Given
        when(authService.autenticarUsuario(any())).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(86400000))
                .andExpect(jsonPath("$.user.id").value("850e8400-e29b-41d4-a716-446655440002"))
                .andExpect(jsonPath("$.user.nome").value("Dr. João Silva"))
                .andExpect(jsonPath("$.user.email").value("joao.silva@medsync.com"))
                .andExpect(jsonPath("$.user.role").value("MEDICO"))
                .andExpect(jsonPath("$.user.permissions").isArray())
                .andExpect(jsonPath("$.user.permissions[0]").value("CRIAR_CONSULTA"))
                .andExpect(jsonPath("$.user.permissions[1]").value("EDITAR_CONSULTA"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando dados de login são inválidos")
    void deveRetornarErro400QuandoDadosDeLoginSaoInvalidos() throws Exception {
        // Given
        LoginRequest loginRequestInvalido = new LoginRequest("", "");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando email é inválido")
    void deveRetornarErro400QuandoEmailEInvalido() throws Exception {
        // Given
        LoginRequest loginRequestEmailInvalido = new LoginRequest("email-invalido", "senha123");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestEmailInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando senha está vazia")
    void deveRetornarErro400QuandoSenhaEstaVazia() throws Exception {
        // Given
        LoginRequest loginRequestSenhaVazia = new LoginRequest("joao.silva@medsync.com", "");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestSenhaVazia)))
                .andExpect(status().isBadRequest());
    }
}

