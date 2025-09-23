package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.application.usecases.AutenticarUsuarioUseCase;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.config.JwtConfig;
import com.medsync.cadastroagendamento.presentation.dto.AutenticarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Mock
    private JwtConfig jwtConfig;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Role role;
    private AutenticarUsuarioRequest request;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"));
        role.setNome("MEDICO");
        role.setPermissoes(List.of());

        usuario = new Usuario();
        usuario.setId(UUID.fromString("850e8400-e29b-41d4-a716-446655440002"));
        usuario.setNome("Dr. João Silva");
        usuario.setEmail("joao.silva@medsync.com");
        usuario.setDataNascimento(LocalDate.of(1975, 5, 15));
        usuario.setRole(role);

        request = new AutenticarUsuarioRequest("joao.silva@medsync.com", "senha123");
    }

    @Test
    @DisplayName("Deve autenticar usuário e retornar AuthResponse com token JWT")
    void deveAutenticarUsuarioERetornarAuthResponse() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        Long expirationTime = 86400000L;

        when(autenticarUsuarioUseCase.executar(request)).thenReturn(usuario);
        when(jwtConfig.generateToken(usuario.getId(), usuario.getEmail(), "MEDICO", usuario.getPermissoes())).thenReturn(token);
        when(jwtConfig.getExpirationTime()).thenReturn(expirationTime);

        // When
        AuthResponse response = authService.autenticarUsuario(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo(token);
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(expirationTime);
        assertThat(response.user()).isNotNull();
        assertThat(response.user().id()).isEqualTo(usuario.getId().toString());
        assertThat(response.user().nome()).isEqualTo(usuario.getNome());
        assertThat(response.user().email()).isEqualTo(usuario.getEmail());
        assertThat(response.user().role()).isEqualTo("MEDICO");
        assertThat(response.user().permissions()).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar permissões do usuário quando role tem permissões")
    void deveRetornarPermissoesDoUsuario() {
        // Given
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        Long expirationTime = 86400000L;

        // Criar permissões para o role
        var permissao1 = new com.medsync.cadastroagendamento.domain.entities.Permissao();
        permissao1.setNome("CRIAR_CONSULTA");
        var permissao2 = new com.medsync.cadastroagendamento.domain.entities.Permissao();
        permissao2.setNome("EDITAR_CONSULTA");
        
        role.setPermissoes(List.of(permissao1, permissao2));

        when(autenticarUsuarioUseCase.executar(request)).thenReturn(usuario);
        when(jwtConfig.generateToken(usuario.getId(), usuario.getEmail(), "MEDICO", usuario.getPermissoes())).thenReturn(token);
        when(jwtConfig.getExpirationTime()).thenReturn(expirationTime);

        // When
        AuthResponse response = authService.autenticarUsuario(request);

        // Then
        assertThat(response.user().permissions()).containsExactly("CRIAR_CONSULTA", "EDITAR_CONSULTA");
    }
}

