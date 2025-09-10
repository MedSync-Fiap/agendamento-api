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
@DisplayName("ValidarMedicoUseCase Tests")
class ValidarMedicoUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private ValidarMedicoUseCase validarMedicoUseCase;

    private Usuario medico;
    private UUID medicoId;

    @BeforeEach
    void setUp() {
        medicoId = UUID.randomUUID();

        medico = new Usuario();
        medico.setId(medicoId);
        medico.setNome("Dr. João Silva");
        medico.setCpf("12345678901");
        medico.setEmail("joao@email.com");
        medico.setSenhaHash("senha_hash");
        medico.setRoleId(UUID.randomUUID());
        medico.setAtivo(true);
        medico.setCriadoEm(LocalDateTime.now().minusDays(1));
        medico.setAtualizadoEm(LocalDateTime.now().minusDays(1));
    }

    @Test
    @DisplayName("Deve validar médico existente com sucesso")
    void deveValidarMedicoExistenteComSucesso() {
        // Given
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));

        // When & Then
        assertThatCode(() -> validarMedicoUseCase.executar(medicoId))
                .doesNotThrowAnyException();

        verify(usuarioGateway).buscarPorId(medicoId);
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando médico não existe")
    void deveLancarUsuarioNaoEncontradoExceptionQuandoMedicoNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> validarMedicoUseCase.executar(medicoId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + medicoId);

        verify(usuarioGateway).buscarPorId(medicoId);
    }
}
