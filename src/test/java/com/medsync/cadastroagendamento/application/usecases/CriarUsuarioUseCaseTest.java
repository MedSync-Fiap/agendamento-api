package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.CpfJaExisteException;
import com.medsync.cadastroagendamento.application.exceptions.EmailJaExisteException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CriarUsuarioUseCase Tests")
class CriarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CriarUsuarioUseCase criarUsuarioUseCase;

    private CriarUsuarioRequest request;
    private Usuario usuarioSalvo;

    @BeforeEach
    void setUp() {
        request = new CriarUsuarioRequest(
                "João Silva",
                "12345678901",
                "joao@email.com",
                "senha123",
                LocalDate.of(1990, 5, 15),
                UUID.randomUUID()
        );

        usuarioSalvo = new Usuario();
        usuarioSalvo.setId(UUID.randomUUID());
        usuarioSalvo.setNome("João Silva");
        usuarioSalvo.setCpf("12345678901");
        usuarioSalvo.setEmail("joao@email.com");
        usuarioSalvo.setSenhaHash("hash_da_senha");
        usuarioSalvo.setRoleId(request.roleId());
        usuarioSalvo.setAtivo(true);
        usuarioSalvo.setCriadoEm(LocalDateTime.now());
        usuarioSalvo.setAtualizadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso quando dados são válidos")
    void deveCriarUsuarioComSucesso() {
        // Given
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
        when(usuarioGateway.existePorCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash_da_senha");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

        // When
        Usuario resultado = criarUsuarioUseCase.executar(request);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getCpf()).isEqualTo("12345678901");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        assertThat(resultado.getSenhaHash()).isEqualTo("hash_da_senha");
        assertThat(resultado.getRoleId()).isEqualTo(request.roleId());
        assertThat(resultado.isAtivo()).isTrue();

        verify(usuarioGateway).existePorEmail("joao@email.com");
        verify(usuarioGateway).existePorCpf("12345678901");
        verify(passwordEncoder).encode("senha123");
        verify(usuarioGateway).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar EmailJaExisteException quando email já existe")
    void deveLancarEmailJaExisteException() {
        // Given
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> criarUsuarioUseCase.executar(request))
                .isInstanceOf(EmailJaExisteException.class)
                .hasMessage("Email já está em uso: joao@email.com");

        verify(usuarioGateway).existePorEmail("joao@email.com");
        verify(usuarioGateway, never()).existePorCpf(anyString());
        verify(usuarioGateway, never()).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar CpfJaExisteException quando CPF já existe")
    void deveLancarCpfJaExisteException() {
        // Given
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
        when(usuarioGateway.existePorCpf(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> criarUsuarioUseCase.executar(request))
                .isInstanceOf(CpfJaExisteException.class)
                .hasMessage("CPF já está em uso: 12345678901");

        verify(usuarioGateway).existePorEmail("joao@email.com");
        verify(usuarioGateway).existePorCpf("12345678901");
        verify(usuarioGateway, never()).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve criptografar a senha antes de salvar")
    void deveCriptografarSenhaAntesDeSalvar() {
        // Given
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
        when(usuarioGateway.existePorCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash_criptografado");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

        // When
        criarUsuarioUseCase.executar(request);

        // Then
        verify(passwordEncoder).encode("senha123");
        verify(usuarioGateway).salvar(argThat(usuario -> 
                "hash_criptografado".equals(usuario.getSenhaHash())
        ));
    }

    @Test
    @DisplayName("Deve definir campos de auditoria corretamente")
    void deveDefinirCamposDeAuditoriaCorretamente() {
        // Given
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
        when(usuarioGateway.existePorCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash_da_senha");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

        // When
        criarUsuarioUseCase.executar(request);

        // Then
        verify(usuarioGateway).salvar(argThat(usuario -> 
                usuario.isAtivo() == true &&
                usuario.getCriadoEm() != null &&
                usuario.getAtualizadoEm() != null
        ));
    }

    @Test
    @DisplayName("Deve gerar UUID único para o usuário")
    void deveGerarUuidUnicoParaUsuario() {
        // Given
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
        when(usuarioGateway.existePorCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash_da_senha");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuarioSalvo);

        // When
        criarUsuarioUseCase.executar(request);

        // Then
        verify(usuarioGateway).salvar(argThat(usuario -> 
                usuario.getId() != null
        ));
    }
}
