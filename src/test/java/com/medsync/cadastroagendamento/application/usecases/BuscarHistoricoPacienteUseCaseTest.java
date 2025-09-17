package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.application.services.HistoricoService;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarHistoricoPacienteUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private HistoricoService historicoService;

    @InjectMocks
    private BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase;

    private UUID pacienteId;
    private UUID medicoId;
    private Usuario paciente;
    private Usuario medico;
    private Role rolePaciente;
    private Role roleMedico;
    private HistoricoPacienteResponse historicoResponse;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.fromString("850e8400-e29b-41d4-a716-446655440004");
        medicoId = UUID.fromString("850e8400-e29b-41d4-a716-446655440002");

        // Setup role paciente
        rolePaciente = new Role();
        rolePaciente.setNome("PACIENTE");
        var permissaoPaciente = new com.medsync.cadastroagendamento.domain.entities.Permissao();
        permissaoPaciente.setNome("VISUALIZAR_HISTORICO");
        rolePaciente.setPermissoes(List.of(permissaoPaciente));

        // Setup role medico
        roleMedico = new Role();
        roleMedico.setNome("MEDICO");
        var permissaoMedico = new com.medsync.cadastroagendamento.domain.entities.Permissao();
        permissaoMedico.setNome("VISUALIZAR_HISTORICO");
        roleMedico.setPermissoes(List.of(permissaoMedico));

        // Setup paciente
        paciente = new Usuario();
        paciente.setId(pacienteId);
        paciente.setNome("Paciente Ana Costa");
        paciente.setEmail("ana.costa@medsync.com");
        paciente.setDataNascimento(LocalDate.of(1990, 12, 10));
        paciente.setRole(rolePaciente);

        // Setup medico
        medico = new Usuario();
        medico.setId(medicoId);
        medico.setNome("Dr. João Silva");
        medico.setEmail("joao.silva@medsync.com");
        medico.setDataNascimento(LocalDate.of(1975, 5, 15));
        medico.setRole(roleMedico);

        // Setup historico response
        historicoResponse = new HistoricoPacienteResponse(
            pacienteId,
            "Paciente Ana Costa",
            "12345678904",
            "ana.costa@medsync.com",
            List.of()
        );
    }

    @Test
    @DisplayName("Deve buscar histórico de paciente quando médico tem permissão")
    void deveBuscarHistoricoPacienteQuandoMedicoTemPermissao() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(historicoService.buscarHistoricoPaciente(pacienteId)).thenReturn(historicoResponse);

        // When
        HistoricoPacienteResponse response = buscarHistoricoPacienteUseCase.executar(pacienteId, medicoId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.pacienteId()).isEqualTo(pacienteId);
        assertThat(response.pacienteNome()).isEqualTo("Paciente Ana Costa");
    }

    @Test
    @DisplayName("Deve buscar histórico quando paciente acessa seu próprio histórico")
    void deveBuscarHistoricoQuandoPacienteAcessaProprioHistorico() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(historicoService.buscarHistoricoPaciente(pacienteId)).thenReturn(historicoResponse);

        // When
        HistoricoPacienteResponse response = buscarHistoricoPacienteUseCase.executar(pacienteId, pacienteId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.pacienteId()).isEqualTo(pacienteId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente não existe")
    void deveLancarExcecaoQuandoPacienteNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buscarHistoricoPacienteUseCase.executar(pacienteId, medicoId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + pacienteId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário logado não existe")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buscarHistoricoPacienteUseCase.executar(pacienteId, medicoId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + medicoId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente tenta acessar histórico de outro paciente")
    void deveLancarExcecaoQuandoPacienteTentaAcessarHistoricoDeOutroPaciente() {
        // Given
        UUID outroPacienteId = UUID.fromString("850e8400-e29b-41d4-a716-446655440005");
        when(usuarioGateway.buscarPorId(outroPacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));

        // When & Then
        assertThatThrownBy(() -> buscarHistoricoPacienteUseCase.executar(outroPacienteId, pacienteId))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Pacientes só podem visualizar seu próprio histórico");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão para visualizar histórico")
    void deveLancarExcecaoQuandoUsuarioNaoTemPermissaoParaVisualizarHistorico() {
        // Given
        Role roleSemPermissao = new Role();
        roleSemPermissao.setNome("ENFERMEIRO");
        roleSemPermissao.setPermissoes(List.of()); // Sem permissões

        Usuario usuarioSemPermissao = new Usuario();
        usuarioSemPermissao.setId(medicoId);
        usuarioSemPermissao.setRole(roleSemPermissao);

        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(usuarioSemPermissao));

        // When & Then
        assertThatThrownBy(() -> buscarHistoricoPacienteUseCase.executar(pacienteId, medicoId))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Usuário não tem permissão para visualizar histórico");
    }
}

