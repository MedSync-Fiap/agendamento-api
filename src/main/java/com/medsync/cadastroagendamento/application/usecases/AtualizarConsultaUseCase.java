package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoGraphQLClient;
import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoPatientClient;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AtualizarConsultaUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(AtualizarConsultaUseCase.class);
    
    private final HistoricoGraphQLClient historicoGraphQLClient;
    private final HistoricoPatientClient historicoPatientClient;
    private final PublicarNotificacaoUseCase publicarNotificacaoUseCase;
    
    public AtualizarConsultaUseCase(HistoricoGraphQLClient historicoGraphQLClient,
                                  HistoricoPatientClient historicoPatientClient,
                                  PublicarNotificacaoUseCase publicarNotificacaoUseCase) {
        this.historicoGraphQLClient = historicoGraphQLClient;
        this.historicoPatientClient = historicoPatientClient;
        this.publicarNotificacaoUseCase = publicarNotificacaoUseCase;
    }
    
    public void executar(UUID consultaId,
                        AtualizarConsultaRequest request, UUID usuarioLogadoId) {
        
        logger.info("Iniciando atualização da consulta {} pelo usuário {}", consultaId, usuarioLogadoId);
        
        if (consultaId == null) {
            throw new IllegalArgumentException("ID da consulta é obrigatório");
        }
        
        try {
            StatusConsulta.valueOf(request.status());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + request.status());
        }
        
        // Usar dados do request diretamente (mais simples e eficiente)
        UUID pacienteId = request.pacienteId();
        UUID medicoId = request.medicoId();
        
        if (pacienteId == null || medicoId == null) {
            throw new IllegalArgumentException("pacienteId e medicoId são obrigatórios para atualização de consulta");
        }
        
        logger.info("Atualizando consulta {} - Paciente: {}, Médico: {}", consultaId, pacienteId, medicoId);
        
        logger.debug("Dados validados - Consulta: {}, Paciente: {}, Médico: {}, Especialidade: {}, Data: {}, Status: {}", 
                    consultaId, pacienteId, medicoId, request.especialidadeId(), request.dataHora(), request.status());

        try {
            historicoGraphQLClient.atualizarConsulta(
                consultaId,
                pacienteId,
                medicoId,
                request.especialidadeId(),
                usuarioLogadoId,
                request.dataHora(),
                request.observacoes(),
                request.status()
            );
            logger.info("Consulta {} atualizada no histórico com sucesso", consultaId);
        } catch (Exception e) {
            logger.error("Erro ao atualizar consulta {} no histórico", consultaId, e);
            throw new RuntimeException("Falha ao atualizar consulta no histórico", e);
        }
        
        // Criar objeto consulta para notificação
        Consulta consulta = new Consulta();
        consulta.setId(consultaId);
        consulta.setPacienteId(pacienteId);
        consulta.setMedicoId(medicoId);
        consulta.setEspecialidadeId(request.especialidadeId());
        consulta.setCriadoPorId(usuarioLogadoId);
        consulta.setDataHora(request.dataHora());
        consulta.setObservacoes(request.observacoes());
        consulta.setStatus(StatusConsulta.valueOf(request.status()));
        
        // Identificar campos alterados
        Map<String, Object> alteracoes = new HashMap<>();
        if (request.dataHora() != null) {
            alteracoes.put("dataHora", request.dataHora());
        }
        if (request.observacoes() != null) {
            alteracoes.put("observacoes", request.observacoes());
        }
        if (request.status() != null) {
            alteracoes.put("status", request.status());
        }
        if (request.especialidadeId() != null) {
            alteracoes.put("especialidadeId", request.especialidadeId());
        }
        
        logger.debug("Campos alterados identificados: {}", alteracoes.keySet());
        
        try {
            publicarNotificacaoUseCase.publicarConsultaEditada(consulta, usuarioLogadoId, alteracoes);
            logger.info("Notificação de consulta editada publicada com sucesso: {}", consultaId);
        } catch (Exception e) {
            logger.error("Erro ao publicar notificação de consulta editada: {}", consultaId, e);
        }
        
        logger.info("Atualização da consulta {} concluída com sucesso", consultaId);
    }
}
