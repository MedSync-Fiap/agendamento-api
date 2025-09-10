package com.medsync.cadastroagendamento.application.usecases;

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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarPacientesUseCase Tests")
class BuscarPacientesUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private BuscarPacientesUseCase buscarPacientesUseCase;

    private List<Usuario> pacientes;

    @BeforeEach
    void setUp() {
        Usuario paciente1 = new Usuario();
        paciente1.setId(UUID.randomUUID());
        paciente1.setNome("João Silva");
        paciente1.setCpf("12345678901");
        paciente1.setEmail("joao@email.com");
        paciente1.setSenhaHash("senha_hash");
        paciente1.setRoleId(UUID.randomUUID());
        paciente1.setAtivo(true);
        paciente1.setCriadoEm(LocalDateTime.now().minusDays(1));
        paciente1.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        Usuario paciente2 = new Usuario();
        paciente2.setId(UUID.randomUUID());
        paciente2.setNome("Maria Santos");
        paciente2.setCpf("98765432109");
        paciente2.setEmail("maria@email.com");
        paciente2.setSenhaHash("senha_hash");
        paciente2.setRoleId(UUID.randomUUID());
        paciente2.setAtivo(true);
        paciente2.setCriadoEm(LocalDateTime.now().minusDays(2));
        paciente2.setAtualizadoEm(LocalDateTime.now().minusDays(2));

        pacientes = Arrays.asList(paciente1, paciente2);
    }

    @Test
    @DisplayName("Deve buscar pacientes com sucesso")
    void deveBuscarPacientesComSucesso() {
        // Given
        when(usuarioGateway.buscarPacientes()).thenReturn(pacientes);

        // When
        List<Usuario> resultado = buscarPacientesUseCase.executar();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNome()).isEqualTo("João Silva");
        assertThat(resultado.get(1).getNome()).isEqualTo("Maria Santos");

        verify(usuarioGateway).buscarPacientes();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há pacientes")
    void deveRetornarListaVaziaQuandoNaoHaPacientes() {
        // Given
        when(usuarioGateway.buscarPacientes()).thenReturn(Arrays.asList());

        // When
        List<Usuario> resultado = buscarPacientesUseCase.executar();

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(usuarioGateway).buscarPacientes();
    }
}
