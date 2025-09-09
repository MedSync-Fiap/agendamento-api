package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.EmailJaExisteException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AtualizarUsuarioUseCase Tests")
class AtualizarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AtualizarUsuarioUseCase atualizarUsuarioUseCase;

    private Usuario usuario;
    private UUID usuarioId;
    private AtualizarUsuarioUseCase.AtualizarUsuarioRequest request;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao@email.com");
        usuario.setSenhaHash("hash_antigo");
        usuario.setRoleId(UUID.randomUUID());
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now().minusDays(1));
        usuario.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        request = new AtualizarUsuarioUseCase.AtualizarUsuarioRequest(
                "João Silva Atualizado",
                "joao.novo@email.com",
                "nova_senha123",
                UUID.randomUUID()
        );
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso quando dados são válidos")
    void deveAtualizarUsuarioComSucesso() {
        // Given
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("novo_hash");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = atualizarUsuarioUseCase.executar(usuarioId, request);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva Atualizado");
        assertThat(resultado.getEmail()).isEqualTo("joao.novo@email.com");
        assertThat(resultado.getSenhaHash()).isEqualTo("novo_hash");

        verify(usuarioGateway).buscarPorId(usuarioId);
        verify(usuarioGateway).existePorEmail("joao.novo@email.com");
        verify(passwordEncoder).encode("nova_senha123");
        verify(usuarioGateway).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando usuário não existe")
    void deveLancarUsuarioNaoEncontradoException() {
        // Given
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> atualizarUsuarioUseCase.executar(usuarioId, request))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + usuarioId);

        verify(usuarioGateway).buscarPorId(usuarioId);
        verify(usuarioGateway, never()).existePorEmail(anyString());
        verify(usuarioGateway, never()).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar EmailJaExisteException quando novo email já existe")
    void deveLancarEmailJaExisteException() {
        // Given
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioGateway.existePorEmail("joao.novo@email.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> atualizarUsuarioUseCase.executar(usuarioId, request))
                .isInstanceOf(EmailJaExisteException.class)
                .hasMessage("Email já está em uso: joao.novo@email.com");

        verify(usuarioGateway).buscarPorId(usuarioId);
        verify(usuarioGateway).existePorEmail("joao.novo@email.com");
        verify(usuarioGateway, never()).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve permitir manter o mesmo email do usuário")
    void devePermitirManterMesmoEmailDoUsuario() {
        // Given
        AtualizarUsuarioUseCase.AtualizarUsuarioRequest requestMesmoEmail = 
                new AtualizarUsuarioUseCase.AtualizarUsuarioRequest(
                        "João Silva Atualizado",
                        "joao@email.com", // mesmo email
                        "nova_senha123",
                        null
                );

        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode(anyString())).thenReturn("novo_hash");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = atualizarUsuarioUseCase.executar(usuarioId, requestMesmoEmail);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");

        verify(usuarioGateway).buscarPorId(usuarioId);
        verify(usuarioGateway, never()).existePorEmail(anyString());
        verify(usuarioGateway).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas nome quando outros campos não são fornecidos")
    void deveAtualizarApenasNomeQuandoOutrosCamposNaoFornecidos() {
        // Given
        AtualizarUsuarioUseCase.AtualizarUsuarioRequest requestApenasNome = 
                new AtualizarUsuarioUseCase.AtualizarUsuarioRequest(
                        "João Silva Atualizado",
                        null,
                        null,
                        null
                );

        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = atualizarUsuarioUseCase.executar(usuarioId, requestApenasNome);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva Atualizado");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com"); // não alterado
        assertThat(resultado.getSenhaHash()).isEqualTo("hash_antigo"); // não alterado

        verify(usuarioGateway).buscarPorId(usuarioId);
        verify(usuarioGateway, never()).existePorEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioGateway).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas email quando outros campos não são fornecidos")
    void deveAtualizarApenasEmailQuandoOutrosCamposNaoFornecidos() {
        // Given
        AtualizarUsuarioUseCase.AtualizarUsuarioRequest requestApenasEmail = 
                new AtualizarUsuarioUseCase.AtualizarUsuarioRequest(
                        null,
                        "novo@email.com",
                        null,
                        null
                );

        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioGateway.existePorEmail("novo@email.com")).thenReturn(false);
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = atualizarUsuarioUseCase.executar(usuarioId, requestApenasEmail);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva"); // não alterado
        assertThat(resultado.getEmail()).isEqualTo("novo@email.com");
        assertThat(resultado.getSenhaHash()).isEqualTo("hash_antigo"); // não alterado

        verify(usuarioGateway).buscarPorId(usuarioId);
        verify(usuarioGateway).existePorEmail("novo@email.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioGateway).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas senha quando outros campos não são fornecidos")
    void deveAtualizarApenasSenhaQuandoOutrosCamposNaoFornecidos() {
        // Given
        AtualizarUsuarioUseCase.AtualizarUsuarioRequest requestApenasSenha = 
                new AtualizarUsuarioUseCase.AtualizarUsuarioRequest(
                        null,
                        null,
                        "nova_senha123",
                        null
                );

        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("nova_senha123")).thenReturn("novo_hash");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuario);

        // When
        Usuario resultado = atualizarUsuarioUseCase.executar(usuarioId, requestApenasSenha);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("João Silva"); // não alterado
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com"); // não alterado
        assertThat(resultado.getSenhaHash()).isEqualTo("novo_hash");

        verify(usuarioGateway).buscarPorId(usuarioId);
        verify(usuarioGateway, never()).existePorEmail(anyString());
        verify(passwordEncoder).encode("nova_senha123");
        verify(usuarioGateway).salvar(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar campo atualizadoEm quando usuário é modificado")
    void deveAtualizarCampoAtualizadoEmQuandoUsuarioModificado() {
        // Given
        LocalDateTime dataAnterior = usuario.getAtualizadoEm();
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("novo_hash");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuario);

        // When
        atualizarUsuarioUseCase.executar(usuarioId, request);

        // Then
        verify(usuarioGateway).salvar(argThat(usuarioSalvo -> 
                usuarioSalvo.getAtualizadoEm() != null &&
                !usuarioSalvo.getAtualizadoEm().equals(dataAnterior)
        ));
    }

    @Test
    @DisplayName("Deve manter campos não alterados quando apenas alguns campos são atualizados")
    void deveManterCamposNaoAlteradosQuandoApenasAlgunsCamposSaoAtualizados() {
        // Given
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioGateway.existePorEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("novo_hash");
        when(usuarioGateway.salvar(any(Usuario.class))).thenReturn(usuario);

        // When
        atualizarUsuarioUseCase.executar(usuarioId, request);

        // Then
        verify(usuarioGateway).salvar(argThat(usuarioSalvo -> 
                usuarioSalvo.getId().equals(usuarioId) &&
                usuarioSalvo.getCpf().equals("12345678901") &&
                usuarioSalvo.getRoleId().equals(usuario.getRoleId()) &&
                usuarioSalvo.isAtivo() == true &&
                usuarioSalvo.getCriadoEm().equals(usuario.getCriadoEm())
        ));
    }
}
