package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.ConflitoHorarioException;
import com.medsync.cadastroagendamento.application.exceptions.ConsultaNaoEncontradaException;
import com.medsync.cadastroagendamento.application.exceptions.ConsultaNaoPodeSerEditadaException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import com.medsync.cadastroagendamento.infrastructure.config.properties.RabbitMQProperties;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AtualizarConsultaUseCase Tests")
class AtualizarConsultaUseCaseTest {

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
    private ValidarConsultaUseCase validarConsultaUseCase;

    @Mock
    private PublicarEventoConsultaUseCase publicarEventoConsultaUseCase;

    @InjectMocks
    private AtualizarConsultaUseCase atualizarConsultaUseCase;

    private Consulta consulta;
    private UUID consultaId;
    private UUID medicoId;
    private UUID novoMedicoId;
    private LocalDateTime dataHora;
    private LocalDateTime novaDataHora;
    private AtualizarConsultaRequest request;
    private Usuario novoMedico;

    @BeforeEach
    void setUp() {
        consultaId = UUID.randomUUID();
        medicoId = UUID.randomUUID();
        novoMedicoId = UUID.randomUUID();
        dataHora = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        novaDataHora = LocalDateTime.now().plusDays(2).withHour(10).withMinute(30);

        consulta = new Consulta();
        consulta.setId(consultaId);
        consulta.setPacienteId(UUID.randomUUID());
        consulta.setMedicoId(medicoId);
        consulta.setCriadoPorId(UUID.randomUUID());
        consulta.setDataHora(dataHora);
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setObservacoes("Consulta de rotina");
        consulta.setCriadoEm(LocalDateTime.now().minusDays(1));
        consulta.setAtualizadoEm(LocalDateTime.now().minusDays(1));

        request = new AtualizarConsultaRequest(
                novoMedicoId,
                novaDataHora,
                "Nova observação",
                UUID.randomUUID()
        );

        novoMedico = new Usuario();
        novoMedico.setId(novoMedicoId);
        novoMedico.setNome("Dr. João Silva");
        novoMedico.setEmail("joao@email.com");

        // Mock RabbitMQ properties
        when(appProperties.getRabbitmq()).thenReturn(rabbitmq);
        when(rabbitmq.getExchangeConsultas()).thenReturn("ex_consultas");
        when(rabbitmq.getRoutingKeyHistorico()).thenReturn("consulta.historico");
        when(rabbitmq.getRoutingKeyNotificacoes()).thenReturn("consulta.notificacao");

        // Configurar mocks dos use cases de validação para não lançar exceções por padrão
        doNothing().when(validarConsultaUseCase).validarAtualizacaoConsulta(any(), any(), any());
        doNothing().when(publicarEventoConsultaUseCase).publicarConsultaEditada(any(), any(), any());
    }

    @Test
    @DisplayName("Deve atualizar consulta com sucesso quando dados são válidos")
    void deveAtualizarConsultaComSucesso() {
        // Given
        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(usuarioGateway.buscarPorId(novoMedicoId)).thenReturn(Optional.of(novoMedico));
        when(consultaGateway.existeConsultaNoHorarioExcluindo(novoMedicoId, novaDataHora, consultaId))
                .thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consulta);

        // When
        Consulta resultado = atualizarConsultaUseCase.executar(consultaId, request, UUID.randomUUID());

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getMedicoId()).isEqualTo(novoMedicoId);
        assertThat(resultado.getDataHora()).isEqualTo(novaDataHora);
        assertThat(resultado.getObservacoes()).isEqualTo("Nova observação");

        verify(consultaGateway).buscarPorId(consultaId);
        verify(validarConsultaUseCase).validarAtualizacaoConsulta(consultaId, novoMedicoId, novaDataHora);
        verify(consultaGateway).salvar(any(Consulta.class));
        verify(publicarEventoConsultaUseCase).publicarConsultaEditada(any(Consulta.class), any(UUID.class), any(Map.class));
    }

    @Test
    @DisplayName("Deve lançar ConsultaNaoEncontradaException quando consulta não existe")
    void deveLancarConsultaNaoEncontradaExceptionQuandoConsultaNaoExiste() {
        // Given
        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> atualizarConsultaUseCase.executar(consultaId, request, UUID.randomUUID()))
                .isInstanceOf(ConsultaNaoEncontradaException.class)
                .hasMessage("Consulta não encontrada com ID: " + consultaId);

        verify(consultaGateway).buscarPorId(consultaId);
        verify(usuarioGateway, never()).buscarPorId(any());
        verify(consultaGateway, never()).existeConsultaNoHorarioExcluindo(any(), any(), any());
        verify(consultaGateway, never()).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar ConsultaNaoPodeSerEditadaException quando consulta não pode ser editada")
    void deveLancarConsultaNaoPodeSerEditadaExceptionQuandoConsultaNaoPodeSerEditada() {
        // Given
        consulta.setStatus(StatusConsulta.REALIZADA); // Status que não permite edição
        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));

        // When & Then
        assertThatThrownBy(() -> atualizarConsultaUseCase.executar(consultaId, request, UUID.randomUUID()))
                .isInstanceOf(ConsultaNaoPodeSerEditadaException.class)
                .hasMessage("Consulta não pode ser editada com ID: " + consultaId);

        verify(consultaGateway).buscarPorId(consultaId);
        verify(usuarioGateway, never()).buscarPorId(any());
        verify(consultaGateway, never()).existeConsultaNoHorarioExcluindo(any(), any(), any());
        verify(consultaGateway, never()).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException quando novo médico não existe")
    void deveLancarUsuarioNaoEncontradoExceptionQuandoNovoMedicoNaoExiste() {
        // Given
        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(usuarioGateway.buscarPorId(novoMedicoId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> atualizarConsultaUseCase.executar(consultaId, request, UUID.randomUUID()))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado com ID: " + novoMedicoId);

        verify(consultaGateway).buscarPorId(consultaId);
        verify(usuarioGateway).buscarPorId(novoMedicoId);
        verify(consultaGateway, never()).existeConsultaNoHorarioExcluindo(any(), any(), any());
        verify(consultaGateway, never()).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve lançar ConflitoHorarioException quando novo horário já está ocupado")
    void deveLancarConflitoHorarioExceptionQuandoNovoHorarioJaEstaOcupado() {
        // Given
        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(usuarioGateway.buscarPorId(novoMedicoId)).thenReturn(Optional.of(novoMedico));
        doThrow(new ConflitoHorarioException(novoMedicoId, novaDataHora))
                .when(validarConsultaUseCase).validarAtualizacaoConsulta(any(), any(), any());

        // When & Then
        assertThatThrownBy(() -> atualizarConsultaUseCase.executar(consultaId, request, UUID.randomUUID()))
                .isInstanceOf(ConflitoHorarioException.class)
                .hasMessage("Já existe uma consulta agendada para o médico " + novoMedicoId + " no horário " + novaDataHora);

        verify(consultaGateway).buscarPorId(consultaId);
        verify(usuarioGateway).buscarPorId(novoMedicoId);
        verify(validarConsultaUseCase).validarAtualizacaoConsulta(consultaId, novoMedicoId, novaDataHora);
        verify(consultaGateway, never()).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas médico quando outros campos não são fornecidos")
    void deveAtualizarApenasMedicoQuandoOutrosCamposNaoSaoFornecidos() {
        // Given
        AtualizarConsultaRequest requestApenasMedico = 
                new AtualizarConsultaRequest(
                        novoMedicoId,
                        null,
                        null,
                        UUID.randomUUID()
                );

        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(usuarioGateway.buscarPorId(novoMedicoId)).thenReturn(Optional.of(novoMedico));
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consulta);

        // When
        Consulta resultado = atualizarConsultaUseCase.executar(consultaId, requestApenasMedico, UUID.randomUUID());

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getMedicoId()).isEqualTo(novoMedicoId);
        assertThat(resultado.getDataHora()).isEqualTo(dataHora); // não alterado
        assertThat(resultado.getObservacoes()).isEqualTo("Consulta de rotina"); // não alterado

        verify(consultaGateway).buscarPorId(consultaId);
        verify(usuarioGateway).buscarPorId(novoMedicoId);
        verify(consultaGateway, never()).existeConsultaNoHorarioExcluindo(any(), any(), any());
        verify(consultaGateway).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas data/hora quando outros campos não são fornecidos")
    void deveAtualizarApenasDataHoraQuandoOutrosCamposNaoSaoFornecidos() {
        // Given
        AtualizarConsultaRequest requestApenasDataHora = 
                new AtualizarConsultaRequest(
                        null,
                        novaDataHora,
                        null,
                        UUID.randomUUID()
                );

        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(consultaGateway.existeConsultaNoHorarioExcluindo(medicoId, novaDataHora, consultaId))
                .thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consulta);

        // When
        Consulta resultado = atualizarConsultaUseCase.executar(consultaId, requestApenasDataHora, UUID.randomUUID());

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getMedicoId()).isEqualTo(medicoId); // não alterado
        assertThat(resultado.getDataHora()).isEqualTo(novaDataHora);
        assertThat(resultado.getObservacoes()).isEqualTo("Consulta de rotina"); // não alterado

        verify(consultaGateway).buscarPorId(consultaId);
        verify(validarConsultaUseCase).validarAtualizacaoConsulta(consultaId, medicoId, novaDataHora);
        verify(consultaGateway).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve atualizar apenas observações quando outros campos não são fornecidos")
    void deveAtualizarApenasObservacoesQuandoOutrosCamposNaoSaoFornecidos() {
        // Given
        AtualizarConsultaRequest requestApenasObservacoes = 
                new AtualizarConsultaRequest(
                        null,
                        null,
                        "Nova observação",
                        UUID.randomUUID()
                );

        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consulta);

        // When
        Consulta resultado = atualizarConsultaUseCase.executar(consultaId, requestApenasObservacoes, UUID.randomUUID());

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getMedicoId()).isEqualTo(medicoId); // não alterado
        assertThat(resultado.getDataHora()).isEqualTo(dataHora); // não alterado
        assertThat(resultado.getObservacoes()).isEqualTo("Nova observação");

        verify(consultaGateway).buscarPorId(consultaId);
        verify(usuarioGateway, never()).buscarPorId(any());
        verify(consultaGateway, never()).existeConsultaNoHorarioExcluindo(any(), any(), any());
        verify(consultaGateway).salvar(any(Consulta.class));
    }

    @Test
    @DisplayName("Deve publicar eventos para RabbitMQ quando há alterações")
    void devePublicarEventosParaRabbitMQQuandoHaAlteracoes() {
        // Given
        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(usuarioGateway.buscarPorId(novoMedicoId)).thenReturn(Optional.of(novoMedico));
        when(consultaGateway.existeConsultaNoHorarioExcluindo(novoMedicoId, novaDataHora, consultaId))
                .thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consulta);

        // When
        atualizarConsultaUseCase.executar(consultaId, request, UUID.randomUUID());

        // Then
        verify(publicarEventoConsultaUseCase).publicarConsultaEditada(any(Consulta.class), any(UUID.class), any(Map.class));
    }

    @Test
    @DisplayName("Não deve publicar eventos quando não há alterações")
    void naoDevePublicarEventosQuandoNaoHaAlteracoes() {
        // Given
        AtualizarConsultaRequest requestSemAlteracoes = 
                new AtualizarConsultaRequest(
                        null,
                        null,
                        null,
                        UUID.randomUUID()
                );

        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consulta);

        // When
        atualizarConsultaUseCase.executar(consultaId, requestSemAlteracoes, UUID.randomUUID());

        // Then
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), (Object) any());
    }

    @Test
    @DisplayName("Deve manter campos não alterados quando apenas alguns campos são atualizados")
    void deveManterCamposNaoAlteradosQuandoApenasAlgunsCamposSaoAtualizados() {
        // Given
        when(consultaGateway.buscarPorId(consultaId)).thenReturn(Optional.of(consulta));
        when(usuarioGateway.buscarPorId(novoMedicoId)).thenReturn(Optional.of(novoMedico));
        when(consultaGateway.existeConsultaNoHorarioExcluindo(novoMedicoId, novaDataHora, consultaId))
                .thenReturn(false);
        when(consultaGateway.salvar(any(Consulta.class))).thenReturn(consulta);

        // When
        atualizarConsultaUseCase.executar(consultaId, request, UUID.randomUUID());

        // Then
        verify(consultaGateway).salvar(argThat(consultaSalva -> 
                consultaSalva.getId().equals(consultaId) &&
                consultaSalva.getPacienteId().equals(consulta.getPacienteId()) &&
                consultaSalva.getCriadoPorId().equals(consulta.getCriadoPorId()) &&
                consultaSalva.getStatus().equals(StatusConsulta.AGENDADA) &&
                consultaSalva.getCriadoEm().equals(consulta.getCriadoEm())
        ));
    }
}
