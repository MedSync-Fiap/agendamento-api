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
@DisplayName("ValidarPacienteUseCase Tests")
class ValidarPacienteUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private ValidarPacienteUseCase validarPacienteUseCase;

    private Usuario paciente;
    private UUID pacienteId;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();

        paciente = new Usuario();
        paciente.setId(pacienteId);
        paciente.setNome("João Silva");
        paciente.setCpf("12345678901");
        paciente.setEmail("joao@email.com");
        paciente.setSenhaHash("senha_hash");
        paciente.setRoleId(UUID.randomUUID());
        paciente.setAtivo(true);
        paciente.setCriadoEm(LocalDateTime.now().minusDays(1));
        paciente.setAtualizadoEm(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("Deve validar paciente existente com sucesso")
    void deveValidarPacienteExistenteComSucesso() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));

        // When & Then
        assertThatCode(() -> validarPacienteUseCase.executar(pacienteId))
                .doesNotThrowAnyException();

        verify(usuarioGateway).buscarPorId(pacienteId);
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando paciente não existe")
    void deveLancarUsuarioNaoEncontradoExceptionQuandoPacienteNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> validarPacienteUseCase.executar(pacienteId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + pacienteId);

        verify(usuarioGateway).buscarPorId(pacienteId);
    }
}
