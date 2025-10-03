package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.TipoRole;
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
@DisplayName("BuscarUsuarioPorEmailUseCase Tests")
class BuscarUsuarioPorEmailUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarUsuarioPorEmailUseCase buscarUsuarioPorEmailUseCase;

    private Usuario usuario;
    private String email;

    @BeforeEach
    void setUp() {
        email = "joao@email.com";

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setTipo(TipoRole.MEDICO);
        role.setDescricao("Médico");
        role.setCriadoEm(LocalDateTime.now().minusDays(1));
        role.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNome("João Silva");
        usuario.setCpf("12345678901");
        usuario.setEmail(email);
        usuario.setSenhaHash("senha_hash");
        usuario.setRole(role);
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now().minusDays(1));
        usuario.setAtualizadoEm(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void deveBuscarUsuarioPorEmailComSucesso() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = buscarUsuarioPorEmailUseCase.executar(email);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo(email);
        assertThat(resultado.getNome()).isEqualTo("João Silva");
        assertThat(resultado.getCpf()).isEqualTo("12345678901");
        assertThat(resultado.isAtivo()).isTrue();

        verify(usuarioGateway).buscarPorEmail(email);
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando usuário não existe")
    void deveLancarUsuarioNaoEncontradoExceptionQuandoUsuarioNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buscarUsuarioPorEmailUseCase.executar(email))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com email: " + email);

        verify(usuarioGateway).buscarPorEmail(email);
    }
}
