package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.entities.Role;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.domain.enums.TipoRole;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.events.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublicarEventoConsultaUseCase Tests")
class PublicarEventoConsultaUseCaseTest {

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private PublicarEventoConsultaUseCase publicarEventoConsultaUseCase;

    private Consulta consulta;
    private Usuario paciente;
    private Usuario medico;
    private Usuario criadoPor;
    private UUID editadoPorId;

    @BeforeEach
    void setUp() {
        UUID consultaId = UUID.randomUUID();
        UUID pacienteId = UUID.randomUUID();
        UUID medicoId = UUID.randomUUID();
        UUID criadoPorId = UUID.randomUUID();
        editadoPorId = UUID.randomUUID();

        consulta = new Consulta();
        consulta.setId(consultaId);
        consulta.setPacienteId(pacienteId);
        consulta.setMedicoId(medicoId);
        consulta.setCriadoPorId(criadoPorId);
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setObservacoes("Consulta de teste");

        // Criar usuários mock
        paciente = criarUsuario(pacienteId, "Paciente Teste", "12345678901", "paciente@test.com", TipoRole.PACIENTE);
        medico = criarUsuario(medicoId, "Médico Teste", "98765432109", "medico@test.com", TipoRole.MEDICO);
        criadoPor = criarUsuario(criadoPorId, "Admin Teste", "11111111111", "admin@test.com", TipoRole.ADMIN);
    }

    private Usuario criarUsuario(UUID id, String nome, String cpf, String email, TipoRole tipoRole) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setDataNascimento(LocalDate.now().minusYears(30));
        
        Role role = new Role();
        role.setTipo(tipoRole);
        usuario.setRole(role);
        
        return usuario;
    }

    @Test
    @DisplayName("Deve publicar eventos de consulta criada com sucesso")
    void devePublicarEventosDeConsultaCriadaComSucesso() {
        // Given
        when(usuarioGateway.buscarPorId(consulta.getPacienteId())).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(consulta.getMedicoId())).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(consulta.getCriadoPorId())).thenReturn(Optional.of(criadoPor));

        // When
        publicarEventoConsultaUseCase.publicarConsultaCriada(consulta);

        // Then
        verify(usuarioGateway).buscarPorId(consulta.getPacienteId());
        verify(usuarioGateway).buscarPorId(consulta.getMedicoId());
        verify(usuarioGateway).buscarPorId(consulta.getCriadoPorId());
        
        verify(eventPublisher).publishNotificacaoConsulta(any());
        verify(eventPublisher).publishHistoricoConsulta(any());
    }

    @Test
    @DisplayName("Deve publicar eventos de consulta editada com sucesso")
    void devePublicarEventosDeConsultaEditadaComSucesso() {
        // Given
        Map<String, Object> alteracoes = Map.of("observacoes", "Observações atualizadas");
        Usuario editadoPor = criarUsuario(editadoPorId, "Editor Teste", "22222222222", "editor@test.com", TipoRole.MEDICO);
        
        when(usuarioGateway.buscarPorId(consulta.getPacienteId())).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(consulta.getMedicoId())).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(editadoPorId)).thenReturn(Optional.of(editadoPor));

        // When
        publicarEventoConsultaUseCase.publicarConsultaEditada(consulta, editadoPorId, alteracoes);

        // Then
        verify(usuarioGateway).buscarPorId(consulta.getPacienteId());
        verify(usuarioGateway).buscarPorId(consulta.getMedicoId());
        verify(usuarioGateway).buscarPorId(editadoPorId);
        
        verify(eventPublisher).publishNotificacaoConsulta(any());
        verify(eventPublisher).publishHistoricoConsulta(any());
    }

    @Test
    @DisplayName("Deve tratar usuário não encontrado retornando usuário vazio")
    void deveTratarUsuarioNaoEncontradoRetornandoUsuarioVazio() {
        // Given
        when(usuarioGateway.buscarPorId(any(UUID.class))).thenReturn(Optional.empty());

        // When
        publicarEventoConsultaUseCase.publicarConsultaCriada(consulta);

        // Then
        verify(usuarioGateway, times(3)).buscarPorId(any(UUID.class));
        verify(eventPublisher).publishNotificacaoConsulta(any());
        verify(eventPublisher).publishHistoricoConsulta(any());
    }

    @Test
    @DisplayName("Deve buscar dados corretos dos usuários para eventos")
    void deveBuscarDadosCorretosDosUsuariosParaEventos() {
        // Given
        when(usuarioGateway.buscarPorId(consulta.getPacienteId())).thenReturn(Optional.of(paciente));
        when(usuarioGateway.buscarPorId(consulta.getMedicoId())).thenReturn(Optional.of(medico));
        when(usuarioGateway.buscarPorId(consulta.getCriadoPorId())).thenReturn(Optional.of(criadoPor));

        // When
        publicarEventoConsultaUseCase.publicarConsultaCriada(consulta);

        // Then
        verify(usuarioGateway).buscarPorId(consulta.getPacienteId());
        verify(usuarioGateway).buscarPorId(consulta.getMedicoId());
        verify(usuarioGateway).buscarPorId(consulta.getCriadoPorId());
        
        // Verifica que os eventos são publicados com os dados corretos
        verify(eventPublisher).publishNotificacaoConsulta(argThat(evento -> 
            evento.consultaId().equals(consulta.getId()) &&
            evento.pacienteId().equals(consulta.getPacienteId()) &&
            evento.medicoId().equals(consulta.getMedicoId()) &&
            evento.criadoPorId().equals(consulta.getCriadoPorId()) &&
            evento.status().equals(consulta.getStatus().toString()) &&
            evento.tipoEvento().equals("CRIADA")
        ));
        
        verify(eventPublisher).publishHistoricoConsulta(argThat(evento -> 
            evento.consultaId().equals(consulta.getId()) &&
            evento.pacienteId().equals(consulta.getPacienteId()) &&
            evento.medicoId().equals(consulta.getMedicoId()) &&
            evento.usuarioId().equals(consulta.getCriadoPorId()) &&
            evento.pacienteNome().equals(paciente.getNome()) &&
            evento.medicoNome().equals(medico.getNome()) &&
            evento.usuarioNome().equals(criadoPor.getNome()) &&
            evento.tipoEvento().equals("CRIADA")
        ));
    }

    @Test
    @DisplayName("Deve publicar eventos de edição com tipo correto")
    void devePublicarEventosDeEdicaoComTipoCorreto() {
        // Given
        Map<String, Object> alteracoes = Map.of("observacoes", "Observações atualizadas");
        Usuario editadoPor = criarUsuario(editadoPorId, "Editor Teste", "22222222222", "editor@test.com", TipoRole.MEDICO);
        
        when(usuarioGateway.buscarPorId(any(UUID.class))).thenReturn(Optional.of(editadoPor));

        // When
        publicarEventoConsultaUseCase.publicarConsultaEditada(consulta, editadoPorId, alteracoes);

        // Then
        verify(eventPublisher).publishNotificacaoConsulta(argThat(evento -> 
            evento.tipoEvento().equals("EDITADA")
        ));
        
        verify(eventPublisher).publishHistoricoConsulta(argThat(evento -> 
            evento.tipoEvento().equals("EDITADA")
        ));
    }
}
