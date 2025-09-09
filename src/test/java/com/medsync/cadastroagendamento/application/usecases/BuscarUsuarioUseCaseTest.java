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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarUsuarioUseCase Tests")
class BuscarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarUsuarioUseCase buscarUsuarioUseCase;

    private Usuario usuario;
    private UUID usuarioId;
    private String email;
    private String cpf;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        email = "joao@email.com";
        cpf = "12345678901";
        roleId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("João Silva");
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setSenhaHash("hash_da_senha");
        usuario.setRoleId(roleId);
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now());
        usuario.setAtualizadoEm(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorIdComSucesso() {
        // Given
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = buscarUsuarioUseCase.buscarPorId(usuarioId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(usuario);
        verify(usuarioGateway).buscarPorId(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por ID")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorId() {
        // Given
        when(usuarioGateway.buscarPorId(usuarioId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buscarUsuarioUseCase.buscarPorId(usuarioId))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
        verify(usuarioGateway).buscarPorId(usuarioId);
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void deveBuscarUsuarioPorEmailComSucesso() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = buscarUsuarioUseCase.buscarPorEmail(email);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(usuario);
        verify(usuarioGateway).buscarPorEmail(email);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por email")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorEmail() {
        // Given
        when(usuarioGateway.buscarPorEmail(email)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buscarUsuarioUseCase.buscarPorEmail(email))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
        verify(usuarioGateway).buscarPorEmail(email);
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF com sucesso")
    void deveBuscarUsuarioPorCpfComSucesso() {
        // Given
        when(usuarioGateway.buscarPorCpf(cpf)).thenReturn(Optional.of(usuario));

        // When
        Usuario resultado = buscarUsuarioUseCase.buscarPorCpf(cpf);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEqualTo(usuario);
        verify(usuarioGateway).buscarPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado por CPF")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorCpf() {
        // Given
        when(usuarioGateway.buscarPorCpf(cpf)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buscarUsuarioUseCase.buscarPorCpf(cpf))
                .isInstanceOf(UsuarioNaoEncontradoException.class);
        verify(usuarioGateway).buscarPorCpf(cpf);
    }

    @Test
    @DisplayName("Deve buscar usuários por role com sucesso")
    void deveBuscarUsuariosPorRoleComSucesso() {
        // Given
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioGateway.buscarPorRole(roleId)).thenReturn(usuarios);

        // When
        List<Usuario> resultado = buscarUsuarioUseCase.buscarPorRole(roleId);

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(usuario);
        verify(usuarioGateway).buscarPorRole(roleId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum usuário encontrado por role")
    void deveRetornarListaVaziaQuandoNenhumUsuarioEncontradoPorRole() {
        // Given
        when(usuarioGateway.buscarPorRole(roleId)).thenReturn(Arrays.asList());

        // When
        List<Usuario> resultado = buscarUsuarioUseCase.buscarPorRole(roleId);

        // Then
        assertThat(resultado).isEmpty();
        verify(usuarioGateway).buscarPorRole(roleId);
    }

    @Test
    @DisplayName("Deve buscar médicos com sucesso")
    void deveBuscarMedicosComSucesso() {
        // Given
        List<Usuario> medicos = Arrays.asList(usuario);
        when(usuarioGateway.buscarMedicos()).thenReturn(medicos);

        // When
        List<Usuario> resultado = buscarUsuarioUseCase.buscarMedicos();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(usuario);
        verify(usuarioGateway).buscarMedicos();
    }

    @Test
    @DisplayName("Deve buscar pacientes com sucesso")
    void deveBuscarPacientesComSucesso() {
        // Given
        List<Usuario> pacientes = Arrays.asList(usuario);
        when(usuarioGateway.buscarPacientes()).thenReturn(pacientes);

        // When
        List<Usuario> resultado = buscarUsuarioUseCase.buscarPacientes();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(usuario);
        verify(usuarioGateway).buscarPacientes();
    }

    @Test
    @DisplayName("Deve buscar todos os usuários com sucesso")
    void deveBuscarTodosUsuariosComSucesso() {
        // Given
        List<Usuario> todosUsuarios = Arrays.asList(usuario);
        when(usuarioGateway.buscarTodos()).thenReturn(todosUsuarios);

        // When
        List<Usuario> resultado = buscarUsuarioUseCase.buscarTodos();

        // Then
        assertThat(resultado).hasSize(1);
        assertThat(resultado).contains(usuario);
        verify(usuarioGateway).buscarTodos();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum usuário encontrado")
    void deveRetornarListaVaziaQuandoNenhumUsuarioEncontrado() {
        // Given
        when(usuarioGateway.buscarTodos()).thenReturn(Arrays.asList());

        // When
        List<Usuario> resultado = buscarUsuarioUseCase.buscarTodos();

        // Then
        assertThat(resultado).isEmpty();
        verify(usuarioGateway).buscarTodos();
    }

    @Test
    @DisplayName("Deve retornar múltiplos usuários quando encontrados")
    void deveRetornarMultiplosUsuariosQuandoEncontrados() {
        // Given
        Usuario usuario2 = new Usuario();
        usuario2.setId(UUID.randomUUID());
        usuario2.setNome("Maria Santos");
        usuario2.setEmail("maria@email.com");
        
        List<Usuario> usuarios = Arrays.asList(usuario, usuario2);
        when(usuarioGateway.buscarTodos()).thenReturn(usuarios);

        // When
        List<Usuario> resultado = buscarUsuarioUseCase.buscarTodos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(usuario, usuario2);
        verify(usuarioGateway).buscarTodos();
    }
}
