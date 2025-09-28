package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.events.ConsultaHistoricoEvent;
import com.medsync.cadastroagendamento.domain.events.ConsultaNotificacaoEvent;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.events.EventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PublicarEventoConsultaUseCase {
    
    private final EventPublisher eventPublisher;
    private final UsuarioGateway usuarioGateway;
    
    public PublicarEventoConsultaUseCase(EventPublisher eventPublisher, UsuarioGateway usuarioGateway) {
        this.eventPublisher = eventPublisher;
        this.usuarioGateway = usuarioGateway;
    }
    
    public void publicarConsultaCriada(Consulta consulta) {
        // Buscar dados detalhados dos usuários
        Usuario paciente = buscarUsuario(consulta.getPacienteId());
        Usuario medico = buscarUsuario(consulta.getMedicoId());
        Usuario criadoPor = buscarUsuario(consulta.getCriadoPorId());
        
        // Criar evento de notificação (dados básicos)
        ConsultaNotificacaoEvent eventoNotificacao = new ConsultaNotificacaoEvent(
            consulta.getId(),
            consulta.getPacienteId(),
            consulta.getMedicoId(),
            consulta.getCriadoPorId(),
            consulta.getDataHora(),
            consulta.getStatus().toString(),
            consulta.getObservacoes(),
            "CRIADA",
            LocalDateTime.now()
        );
        
        // Criar evento de histórico (dados detalhados)
        ConsultaHistoricoEvent eventoHistorico = new ConsultaHistoricoEvent(
            consulta.getId(),
            consulta.getDataHora(),
            consulta.getStatus().toString(),
            consulta.getObservacoes(),
            "CRIADA",
            LocalDateTime.now(),
            paciente.getId(),
            paciente.getNome(),
            paciente.getCpf(),
            paciente.getEmail(),
            paciente.getDataNascimento(),
            medico.getId(),
            medico.getNome(),
            medico.getCpf(),
            medico.getEmail(),
            "Especialidade não definida", // TODO: Implementar especialidade
            criadoPor.getId(),
            criadoPor.getNome(),
            criadoPor.getEmail(),
            criadoPor.getRole().getTipo().toString()
        );
        
        // Publicar eventos
        eventPublisher.publishNotificacaoConsulta(eventoNotificacao);
        eventPublisher.publishHistoricoConsulta(eventoHistorico);
    }
    
    public void publicarConsultaEditada(Consulta consulta, UUID editadoPorId) {
        // Buscar dados detalhados dos usuários
        Usuario paciente = buscarUsuario(consulta.getPacienteId());
        Usuario medico = buscarUsuario(consulta.getMedicoId());
        Usuario editadoPor = buscarUsuario(editadoPorId);
        
        // Criar evento de notificação (dados básicos)
        ConsultaNotificacaoEvent eventoNotificacao = new ConsultaNotificacaoEvent(
            consulta.getId(),
            consulta.getPacienteId(),
            consulta.getMedicoId(),
            editadoPorId,
            consulta.getDataHora(),
            consulta.getStatus().toString(),
            consulta.getObservacoes(),
            "EDITADA",
            LocalDateTime.now()
        );
        
        // Criar evento de histórico (dados detalhados)
        ConsultaHistoricoEvent eventoHistorico = new ConsultaHistoricoEvent(
            consulta.getId(),
            consulta.getDataHora(),
            consulta.getStatus().toString(),
            consulta.getObservacoes(),
            "EDITADA",
            LocalDateTime.now(),
            paciente.getId(),
            paciente.getNome(),
            paciente.getCpf(),
            paciente.getEmail(),
            paciente.getDataNascimento(),
            medico.getId(),
            medico.getNome(),
            medico.getCpf(),
            medico.getEmail(),
            "Especialidade não definida",
            editadoPor.getId(),
            editadoPor.getNome(),
            editadoPor.getEmail(),
            editadoPor.getRole().getTipo().toString()
        );
        
        // Publicar eventos
        eventPublisher.publishNotificacaoConsulta(eventoNotificacao);
        eventPublisher.publishHistoricoConsulta(eventoHistorico);
    }
    
    private Usuario buscarUsuario(UUID usuarioId) {
        return usuarioGateway.buscarPorId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));
    }
}
