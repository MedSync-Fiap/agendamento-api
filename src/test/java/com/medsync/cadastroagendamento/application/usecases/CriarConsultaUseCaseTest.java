package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.ConflitoHorarioException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import com.medsync.cadastroagendamento.infrastructure.config.properties.RabbitMQProperties;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CriarConsultaUseCase Tests")
class CriarConsultaUseCaseTest {

    @Mock
    private ConsultaGateway consultaGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AppProperties appProperties;

    @Mock
    private RabbitMQProperties rabbitmq;

    @Mock
    private PublicarNotificacaoUseCase publicarNotificacaoUseCase;

    @Mock
    private SalvarHistoricoUseCase salvarHistoricoUseCase;

    @InjectMocks
    private CriarConsultaUseCase criarConsultaUseCase;

    private CriarConsultaRequest request;
    private Usuario paciente;
    private Usuario medico;
    private Usuario criadoPor;
    private Consulta consultaSalva;
    private UUID pacienteId;
    private UUID medicoId;
    private UUID criadoPorId;
    private LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        pacienteId = UUID.randomUUID();
        medicoId = UUID.randomUUID();
        criadoPorId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);

        request = new CriarConsultaRequest(
                pacienteId,
                medicoId,
                dataHora,
                "Consulta de rotina"
        );

        paciente = new Usuario();
        paciente.setId(pacienteId);
        paciente.setNome("João Silva");
        paciente.setEmail("joao@email.com");

        medico = new Usuario();
        medico.setId(medicoId);
        medico.setNome("Dr. Maria Santos");
        medico.setEmail("maria@email.com");

        criadoPor = new Usuario();
        criadoPor.setId(criadoPorId);
        criadoPor.setNome("Enfermeiro Carlos");
        criadoPor.setEmail("carlos@email.com");

        consultaSalva = new Consulta();
        consultaSalva.setId(UUID.randomUUID());
        consultaSalva.setPacienteId(pacienteId);
        consultaSalva.setMedicoId(medicoId);
        consultaSalva.setCriadoPorId(criadoPorId);
        consultaSalva.setDataHora(dataHora);
        consultaSalva.setStatus(StatusConsulta.AGENDADA);
        consultaSalva.setObservacoes("Consulta de rotina");

        when(appProperties.getRabbitmq()).thenReturn(rabbitmq);
        when(rabbitmq.getExchangeConsultas()).thenReturn("ex_consultas");
        when(rabbitmq.getRoutingKeyHistorico()).thenReturn("consulta.historico");
        when(rabbitmq.getRoutingKeyNotificacoes()).thenReturn("consulta.notificacao");

        // Configurar mocks dos use cases para não lançar exceções por padrão
        doNothing().when(publicarNotificacaoUseCase).publicarConsultaCriada(any());
        doNothing().when(salvarHistoricoUseCase).executar(any());
    }

    @Test
    @DisplayName("Deve criar consulta com sucesso quando dados são válidos")
    void deveCriarConsultaComSucesso() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(criadoPorId)).thenReturn(Optional.of(criadoPor));
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consultaSalva);

        // When
        Consulta resultado = criarConsultaUseCase.executar(request, criadoPorId);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getPacienteId()).isEqualTo(pacienteId);
        assertThat(resultado.getMedicoId()).isEqualTo(medicoId);
        assertThat(resultado.getCriadoPorId()).isEqualTo(criadoPorId);
        assertThat(resultado.getDataHora()).isEqualTo(dataHora);
        assertThat(resultado.getStatus()).isEqualTo(StatusConsulta.AGENDADA);
        assertThat(resultado.getObservacoes()).isEqualTo("Consulta de rotina");

        verify(consultaGateway).salvar(any(Consulta.class));
        verify(publicarNotificacaoUseCase).publicarConsultaCriada(any(Consulta.class));
        verify(salvarHistoricoUseCase).executar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando paciente não existe")
    void deveLancarUsuarioNaoEncontradoExceptionQuandoPacienteNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> criarConsultaUseCase.executar(request, criadoPorId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + pacienteId);

        verify(usuarioGateway).buscarPorId(pacienteId);
        verify(consultaGateway, never()).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando médico não existe")
    void deveLancarUsuarioNaoEncontradoExceptionQuandoMedicoNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> criarConsultaUseCase.executar(request, criadoPorId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + medicoId);

        verify(usuarioGateway).buscarPorId(medicoId);
        verify(consultaGateway, never()).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando usuário criador não existe")
    void deveLancarUsuarioNaoEncontradoExceptionQuandoUsuarioCriadorNaoExiste() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(criadoPorId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> criarConsultaUseCase.executar(request, criadoPorId))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + criadoPorId);

        verify(usuarioGateway).buscarPorId(criadoPorId);
        verify(consultaGateway, never()).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar ConflitoHorarioException quando já existe consulta no horário")
    void deveLancarConflitoHorarioExceptionQuandoJaExisteConsultaNoHorario() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(criadoPorId)).thenReturn(Optional.of(criadoPor));
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> criarConsultaUseCase.executar(request, criadoPorId))
                .isInstanceOf(ConflitoHorarioException.class)
                .hasMessage("Já existe uma consulta agendada para o médico " + medicoId + " no horário " + dataHora);

        verify(consultaGateway).existeConsultaNoHorario(medicoId, dataHora);
        verify(consultaGateway, never()).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve publicar eventos para RabbitMQ após criar consulta")
    void devePublicarEventosParaRabbitMQAposCriarConsulta() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(criadoPorId)).thenReturn(Optional.of(criadoPor));
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consultaSalva);

        // When
        criarConsultaUseCase.executar(request, criadoPorId);

        // Then
        verify(publicarNotificacaoUseCase).publicarConsultaCriada(any(Consulta.class));
        verify(salvarHistoricoUseCase).executar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve definir status como AGENDADA ao criar consulta")
    void deveDefinirStatusComoAgendadaAoCriarConsulta() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(criadoPorId)).thenReturn(Optional.of(criadoPor));
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consultaSalva);

        // When
        criarConsultaUseCase.executar(request, criadoPorId);

        // Then
        verify(consultaGateway).salvar(argThat(consulta -> 
                consulta.getStatus() == StatusConsulta.AGENDADA
        ));
    }

    @Test
    @DisplayName("Deve definir campos de auditoria ao criar consulta")
    void deveDefinirCamposDeAuditoriaAoCriarConsulta() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(criadoPorId)).thenReturn(Optional.of(criadoPor));
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consultaSalva);

        // When
        criarConsultaUseCase.executar(request, criadoPorId);

        // Then
        verify(consultaGateway).salvar(argThat(consulta -> 
                consulta.getCriadoEm() != null &&
                consulta.getAtualizadoEm() != null
        ));
    }

    @Test
    @DisplayName("Deve gerar UUID único para a consulta")
    void deveGerarUuidUnicoParaConsulta() {
        // Given
        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(criadoPorId)).thenReturn(Optional.of(criadoPor));
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consultaSalva);

        // When
        criarConsultaUseCase.executar(request, criadoPorId);

        // Then
        verify(consultaGateway).salvar(argThat(consulta -> 
                consulta.getId() != null
        ));
    }

    @Test
    @DisplayName("Deve criar consulta com observações nulas quando não fornecidas")
    void deveCriarConsultaComObservacoesNulasQuandoNaoFornecidas() {
        // Given
        CriarConsultaRequest requestSemObservacoes = 
                new CriarConsultaRequest(
                        pacienteId,
                        medicoId,
                        dataHora,
                        null
                );

        when(usuarioGateway.buscarPorId(pacienteId)).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(medicoId)).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(criadoPorId)).thenReturn(Optional.of(criadoPor));
        when(consultaGateway.existeConsultaNoHorario(medicoId, dataHora)).thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consultaSalva);

        // When
        criarConsultaUseCase.executar(requestSemObservacoes, criadoPorId);

        // Then
        verify(consultaGateway).salvar(argThat(consulta -> 
                consulta.getObservacoes() == null
        ));
    }
}
