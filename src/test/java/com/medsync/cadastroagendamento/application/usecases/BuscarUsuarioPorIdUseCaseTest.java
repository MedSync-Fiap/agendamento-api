package com.medsync.cadastroagendamento.application.usecases;

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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarUsuarioPorIdUseCase Tests")
class BuscarUsuarioPorIdUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;

    private Usuario usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail("joao@email.com");
        usuario.setSenhaHash("senha_hash");
        usuario.setRoleId(UUID.randomUUID());
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now().minusDays(1));
        usuario.setAtualizadoEm(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // Given
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = buscarUsuarioPorIdUseCase.executar(usuarioId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioId);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getEmail()).isEqualTo("joao@email.com");
        assertThat(resultado.getCpf()).isEqualTo("12345678901");
        assertThat(resultado.isAtivo()).isTrue();

        verify(usuarioGateway).buscarPorId(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando usuário não existe")
    void deveLancarUsuarioNaoEncontradoExceptionQuandoUsuarioNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buscarUsuarioPorIdUseCase.executar(usuarioId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + usuarioId);

        verify(usuarioGateway).buscarPorId(usuarioId);
    }
}
