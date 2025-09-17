package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidarPermissaoPacienteUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private ValidarPermissaoPacienteUseCase validarPermissaoPacienteUseCase;

    private UUID pacienteId;
    private UUID enfermeiroId;
    private UUID medicoId;
    private Usuario paciente;
    private Usuario enfermeiro;
    private Usuario medico;
    private Role rolePaciente;
    private Role roleEnfermeiro;
    private Role roleMedico;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.fromString("850e8400-e29b-41d4-a716-446655440004");
        enfermeiroId = UUID.fromString("850e8400-e29b-41d4-a716-446655440003");
        medicoId = UUID.fromString("850e8400-e29b-41d4-a716-446655440002");

        // Setup role paciente
        rolePaciente = new Role();
        rolePaciente.setNome("PACIENTE");
        var permissaoPaciente = new com.medsync.cadastroagendamento.domain.entities.Permissao();
        permissaoPaciente.setNome("VISUALIZAR_CONSULTAS");
        rolePaciente.setPermissoes(List.of(permissaoPaciente));

        // Setup role enfermeiro
        roleEnfermeiro = new Role();
        roleEnfermeiro.setNome("ENFERMEIRO");
        var permissaoEnfermeiro = new com.medsync.cadastroagendamento.domain.entities.Permissao();
        permissaoEnfermeiro.setNome("CRIAR_CONSULTA");
        roleEnfermeiro.setPermissoes(List.of(permissaoEnfermeiro));

        // Setup role medico
        roleMedico = new Role();
        roleMedico.setNome("MEDICO");
        var permissaoMedico = new com.medsync.cadastroagendamento.domain.entities.Permissao();
        permissaoMedico.setNome("CRIAR_CONSULTA");
        roleMedico.setPermissoes(List.of(permissaoMedico));

        // Setup paciente
        paciente = new Usuario();
        paciente.setId(pacienteId);
        paciente.setNome("Paciente Ana Costa");
        paciente.setEmail("ana.costa@medsync.com");
        paciente.setDataNascimento(LocalDate.of(1990, 12, 10));
        paciente.setRole(rolePaciente);

        // Setup enfermeiro
        enfermeiro = new Usuario();
        enfermeiro.setId(enfermeiroId);
        enfermeiro.setNome("Enfermeira Maria Santos");
        enfermeiro.setEmail("maria.santos@medsync.com");
        enfermeiro.setDataNascimento(LocalDate.of(1985, 8, 20));
        enfermeiro.setRole(roleEnfermeiro);

        // Setup medico
        medico = new Usuario();
        medico.setId(medicoId);
        medico.setNome("Dr. João Silva");
        medico.setEmail("joao.silva@medsync.com");
        medico.setDataNascimento(LocalDate.of(1975, 5, 15));
        medico.setRole(roleMedico);
    }

    @Test
    @DisplayName("Deve validar acesso quando enfermeiro acessa dados de paciente")
    void deveValidarAcessoQuandoEnfermeiroAcessaDadosDePaciente() {
        // Given
        when(usuarioGateway.buscarPorId(enfermeiroId)).thenReturn(Optional.of(enfermeiro));

        // When & Then - Não deve lançar exceção
        validarPermissaoPacienteUseCase.validarAcessoPaciente(pacienteId, enfermeiroId);
    }

    @Test
    @DisplayName("Deve validar acesso quando paciente acessa seus próprios dados")
    void deveValidarAcessoQuandoPacienteAcessaPropriosDados() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));

        // When & Then - Não deve lançar exceção
        validarPermissaoPacienteUseCase.validarAcessoPaciente(pacienteId, pacienteId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente tenta acessar dados de outro paciente")
    void deveLancarExcecaoQuandoPacienteTentaAcessarDadosDeOutroPaciente() {
        // Given
        UUID outroPacienteId = UUID.fromString("850e8400-e29b-41d4-a716-446655440005");
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));

        // When & Then
        assertThatThrownBy(() -> validarPermissaoPacienteUseCase.validarAcessoPaciente(outroPacienteId, pacienteId))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Pacientes só podem acessar seus próprios dados");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão para acessar dados de pacientes")
    void deveLancarExcecaoQuandoUsuarioNaoTemPermissaoParaAcessarDadosDePacientes() {
        // Given
        Role roleSemPermissao = new Role();
        roleSemPermissao.setNome("RECEPCIONISTA");
        roleSemPermissao.setPermissoes(List.of()); // Sem permissões

        Usuario usuarioSemPermissao = new Usuario();
        usuarioSemPermissao.setId(UUID.randomUUID());
        usuarioSemPermissao.setRole(roleSemPermissao);

        when(usuarioGateway.buscarPorId(usuarioSemPermissao.getId())).thenReturn(Optional.of(usuarioSemPermissao));

        // When & Then
        assertThatThrownBy(() -> validarPermissaoPacienteUseCase.validarAcessoPaciente(pacienteId, usuarioSemPermissao.getId()))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Usuário não tem permissão para acessar dados de pacientes");
    }

    @Test
    @DisplayName("Deve validar criação de consulta quando enfermeiro tem permissão")
    void deveValidarCriacaoConsultaQuandoEnfermeiroTemPermissao() {
        // Given
        when(usuarioGateway.buscarPorId(enfermeiroId)).thenReturn(Optional.of(enfermeiro));

        // When & Then - Não deve lançar exceção
        validarPermissaoPacienteUseCase.validarCriacaoConsulta(pacienteId, enfermeiroId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente tenta criar consulta")
    void deveLancarExcecaoQuandoPacienteTentaCriarConsulta() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));

        // When & Then
        assertThatThrownBy(() -> validarPermissaoPacienteUseCase.validarCriacaoConsulta(pacienteId, pacienteId))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Pacientes não podem criar consultas");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não tem permissão para criar consultas")
    void deveLancarExcecaoQuandoUsuarioNaoTemPermissaoParaCriarConsultas() {
        // Given
        Role roleSemPermissao = new Role();
        roleSemPermissao.setNome("RECEPCIONISTA");
        roleSemPermissao.setPermissoes(List.of()); // Sem permissões

        Usuario usuarioSemPermissao = new Usuario();
        usuarioSemPermissao.setId(UUID.randomUUID());
        usuarioSemPermissao.setRole(roleSemPermissao);

        when(usuarioGateway.buscarPorId(usuarioSemPermissao.getId())).thenReturn(Optional.of(usuarioSemPermissao));

        // When & Then
        assertThatThrownBy(() -> validarPermissaoPacienteUseCase.validarCriacaoConsulta(pacienteId, usuarioSemPermissao.getId()))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Usuário não tem permissão para criar consultas");
    }

    @Test
    @DisplayName("Deve validar edição de consulta quando enfermeiro tem permissão")
    void deveValidarEdicaoConsultaQuandoEnfermeiroTemPermissao() {
        // Given
        UUID consultaId = UUID.randomUUID();
        when(usuarioGateway.buscarPorId(enfermeiroId)).thenReturn(Optional.of(enfermeiro));

        // When & Then - Não deve lançar exceção
        validarPermissaoPacienteUseCase.validarEdicaoConsulta(consultaId, enfermeiroId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando paciente tenta editar consulta")
    void deveLancarExcecaoQuandoPacienteTentaEditarConsulta() {
        // Given
        UUID consultaId = UUID.randomUUID();
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));

        // When & Then
        assertThatThrownBy(() -> validarPermissaoPacienteUseCase.validarEdicaoConsulta(consultaId, pacienteId))
                .isInstanceOf(SecurityException.class)
                .hasMessage("Pacientes não podem editar consultas");
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        // Given
        UUID usuarioInexistenteId = UUID.randomUUID();
        when(usuarioGateway.buscarPorId(usuarioInexistenteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> validarPermissaoPacienteUseCase.validarAcessoPaciente(pacienteId, usuarioInexistenteId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + usuarioInexistenteId);
    }
}

