package com.medsync.cadastroagendamento.application.usecases;

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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarTodosUsuariosUseCase Tests")
class BuscarTodosUsuariosUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarTodosUsuariosUseCase buscarTodosUsuariosUseCase;

    private List<Usuario> usuarios;

    @BeforeEach
    void setUp() {
        Role role1 = new Role();
        role1.setId(UUID.randomUUID());
        role1.setTipo(TipoRole.MEDICO);
        role1.setDescricao("Médico");
        role1.setCriadoEm(LocalDateTime.now().minusDays(1));
        role1.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        Role role2 = new Role();
        role2.setId(UUID.randomUUID());
        role2.setTipo(TipoRole.PACIENTE);
        role2.setDescricao("Paciente");
        role2.setCriadoEm(LocalDateTime.now().minusDays(2));
        role2.setAtualizadoEm(LocalDateTime.now().minusDays(2));

        Usuario usuario1 = new Usuario();
        usuario1.setId(UUID.randomUUID());
        usuario1.setNome("João Silva");
        usuario1.setCpf("12345678901");
        usuario1.setEmail("joao@email.com");
        usuario1.setSenhaHash("senha_hash");
        usuario1.setRole(role1);
        usuario1.setAtivo(true);
        usuario1.setCriadoEm(LocalDateTime.now().minusDays(1));
        usuario1.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        Usuario usuario2 = new Usuario();
        usuario2.setId(UUID.randomUUID());
        usuario2.setNome("Maria Santos");
        usuario2.setCpf("98765432109");
        usuario2.setEmail("maria@email.com");
        usuario2.setSenhaHash("senha_hash");
        usuario2.setRole(role2);
        usuario2.setAtivo(true);
        usuario2.setCriadoEm(LocalDateTime.now().minusDays(2));
        usuario2.setAtualizadoEm(LocalDateTime.now().minusDays(2));

        usuarios = Arrays.asList(usuario1, usuario2);
    }

    @Test
    @DisplayName("Deve buscar todos os usuários com sucesso")
    void deveBuscarTodosUsuariosComSucesso() {
        // Given
        when(usuarioGateway.buscarTodos()).thenReturn(usuarios);

        // When
        List<Usuario> resultado = buscarTodosUsuariosUseCase.executar();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        assertThat(resultado.get(1).getNome()).isEqualTo("Maria Santos");

        verify(usuarioGateway).buscarTodos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        // Given
        when(usuarioGateway.buscarTodos()).thenReturn(Arrays.asList());

        // When
        List<Usuario> resultado = buscarTodosUsuariosUseCase.executar();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(usuarioGateway).buscarTodos();
    }
}
