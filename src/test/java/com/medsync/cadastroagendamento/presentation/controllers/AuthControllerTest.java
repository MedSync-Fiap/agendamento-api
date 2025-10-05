package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.AutenticarUsuarioUseCase;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.config.TestConfig;
import com.medsync.cadastroagendamento.infrastructure.security.JwtTokenProvider;
import com.medsync.cadastroagendamento.presentation.dto.AutenticarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private AutenticarUsuarioRequest loginRequest;
    private Usuario usuario;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new AutenticarUsuarioRequest("joao.silva@medsync.com", "senha123");
        
        // Setup role
        Role role = new Role();
        role.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));
        role.setTipo(com.medsync.cadastroagendamento.domain.enums.TipoRole.MEDICO);
        
        // Setup usuario
        usuario = new Usuario();
        usuario.setId(UUID.fromString("850e8400-e29b-41d4-a716-446655440002"));
        usuario.setNome("Dr. João Silva");
        usuario.setEmail("joao.silva@medsync.com");
        usuario.setDataNascimento(LocalDate.of(1975, 5, 15));
        usuario.setRole(role);
        
        loginResponse = new LoginResponse(
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            usuario.getId(),
            "Dr. João Silva",
            "joao.silva@medsync.com",
            "MEDICO"
        );
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() throws Exception {
        // Given
        when(autenticarUsuarioUseCase.executar(any())).thenReturn(usuario);
        when(jwtTokenProvider.generateToken(any())).thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."))
                .andExpect(jsonPath("$.usuarioId").value("850e8400-e29b-41d4-a716-446655440002"))
                .andExpect(jsonPath("$.nome").value("Dr. João Silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@medsync.com"))
                .andExpect(jsonPath("$.role").value("MEDICO"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando dados de login são inválidos")
    void deveRetornarErro400QuandoDadosDeLoginSaoInvalidos() throws Exception {
        // Given
        AutenticarUsuarioRequest loginRequestInvalido = new AutenticarUsuarioRequest("", "");

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
        AutenticarUsuarioRequest loginRequestEmailInvalido = new AutenticarUsuarioRequest("email-invalido", "senha123");

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
        AutenticarUsuarioRequest loginRequestSenhaVazia = new AutenticarUsuarioRequest("joao.silva@medsync.com", "");

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestSenhaVazia)))
                .andExpect(status().isBadRequest());
    }
}

