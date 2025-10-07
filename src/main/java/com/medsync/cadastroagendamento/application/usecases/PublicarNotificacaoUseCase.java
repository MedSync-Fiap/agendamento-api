package com.medsync.cadastroagendamento.application.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.exception.EspecialidadeNotFoundException;
import com.medsync.cadastroagendamento.domain.exception.UsuarioNotFoundException;
import com.medsync.cadastroagendamento.domain.gateways.EspecialidadeGateway;
import com.medsync.cadastroagendamento.domain.gateways.TelefoneGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoPatientClient;
import com.medsync.cadastroagendamento.infrastructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class PublicarNotificacaoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(PublicarNotificacaoUseCase.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig rabbitMQConfig;
    private final UsuarioGateway usuarioGateway;
    private final EspecialidadeGateway especialidadeGateway;
    private final TelefoneGateway telefoneGateway;
    private final HistoricoPatientClient historicoPatientClient;
    private final ObjectMapper objectMapper;

    public PublicarNotificacaoUseCase(RabbitTemplate rabbitTemplate,
                                      RabbitMQConfig rabbitMQConfig,
                                      UsuarioGateway usuarioGateway,
                                      EspecialidadeGateway especialidadeGateway,
                                      TelefoneGateway telefoneGateway,
                                      HistoricoPatientClient historicoPatientClient,
                                      ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQConfig = rabbitMQConfig;
        this.usuarioGateway = usuarioGateway;
        this.especialidadeGateway = especialidadeGateway;
        this.telefoneGateway = telefoneGateway;
        this.historicoPatientClient = historicoPatientClient;
        this.objectMapper = objectMapper;
    }

    public void publicarConsultaCriada(Consulta consulta) {
        try {
            logger.info("Publicando notificação de consulta criada: {}", consulta.getId());

            Usuario medico = usuarioGateway.buscarPorId(consulta.getMedicoId())
                    .orElseThrow(() -> UsuarioNotFoundException.byId(consulta.getMedicoId()));
            Especialidade especialidade = especialidadeGateway.buscarPorId(consulta.getEspecialidadeId())
                    .orElseThrow(() -> EspecialidadeNotFoundException.byId(consulta.getEspecialidadeId()));
            Map<String, Object> paciente = historicoPatientClient.buscarPaciente(consulta.getPacienteId());
            if (paciente == null) {
                logger.warn("Paciente não encontrado: {}", consulta.getPacienteId());
                return;
            }

            Map<String, Object> eventoNotificacao = new HashMap<>();
            eventoNotificacao.put("evento", "consulta_criada_notificacao");
            eventoNotificacao.put("consulta_id", consulta.getId().toString());
            eventoNotificacao.put("paciente_nome", paciente.get("name") != null ? paciente.get("name").toString() : "Paciente");
            eventoNotificacao.put("paciente_email", paciente.get("email") != null ? paciente.get("email").toString() : "");
            eventoNotificacao.put("paciente_telefone", paciente.get("telefone") != null ? paciente.get("telefone").toString() : "");
            eventoNotificacao.put("medico_nome", medico != null && medico.getNome() != null ? medico.getNome() : "Médico");
            eventoNotificacao.put("medico_email", medico != null && medico.getEmail() != null ? medico.getEmail() : "");
            // Buscar telefone do médico
            String medicoTelefone = "";
            if (medico != null) {
                try {
                    var telefones = telefoneGateway.buscarPorUsuarioId(medico.getId());
                    if (!telefones.isEmpty()) {
                        medicoTelefone = telefones.get(0).getNumero();
                    }
                } catch (Exception e) {
                    logger.warn("Erro ao buscar telefone do médico {}: {}", medico.getId(), e.getMessage());
                }
            }
            eventoNotificacao.put("medico_telefone", medicoTelefone);
            eventoNotificacao.put("medico_especialidade", medico != null && especialidade.getNome() != null ? especialidade.getNome() : "Especialidade não informada");
            eventoNotificacao.put("data_hora", consulta.getDataHora().toString());
            eventoNotificacao.put("observacoes", consulta.getObservacoes() != null ? consulta.getObservacoes() : "");
            eventoNotificacao.put("status", consulta.getStatus().toString());
            eventoNotificacao.put("timestamp", LocalDateTime.now().toString());

            String jsonEvento = objectMapper.writeValueAsString(eventoNotificacao);
            rabbitTemplate.convertAndSend(
                    rabbitMQConfig.exchangeConsultas().getName(),
                    rabbitMQConfig.routingKeyNotificacoes(),
                    jsonEvento
            );

            logger.info("Notificação de consulta criada publicada com sucesso: {}", consulta.getId());

        } catch (Exception e) {
            logger.error("Erro ao publicar notificação de consulta criada: {}", consulta.getId(), e);
            throw new RuntimeException("Falha ao publicar notificação de consulta criada", e);
        }
    }

    public void publicarConsultaEditada(Consulta consulta, UUID editadoPorId, Map<String, Object> alteracoes) {
        try {
            logger.info("Publicando notificação de consulta editada: {}", consulta.getId());

            Usuario medico = usuarioGateway.buscarPorId(consulta.getMedicoId())
                    .orElseThrow(() -> UsuarioNotFoundException.byId(consulta.getMedicoId()));
            Especialidade especialidade = especialidadeGateway.buscarPorId(consulta.getEspecialidadeId())
                    .orElseThrow(() -> EspecialidadeNotFoundException.byId(consulta.getEspecialidadeId()));
            Map<String, Object> paciente = historicoPatientClient.buscarPaciente(consulta.getPacienteId());
            if (paciente == null) {
                logger.warn("Paciente não encontrado: {}", consulta.getPacienteId());
                return;
            }


            Map<String, Object> eventoNotificacao = new HashMap<>();
            eventoNotificacao.put("evento", "consulta_editada_notificacao");
            eventoNotificacao.put("consulta_id", consulta.getId().toString());
            eventoNotificacao.put("paciente_nome", paciente.get("name") != null ? paciente.get("name").toString() : "Paciente");
            eventoNotificacao.put("paciente_email", paciente.get("email") != null ? paciente.get("email").toString() : "");
            eventoNotificacao.put("paciente_telefone", paciente.get("telefone") != null ? paciente.get("telefone").toString() : "");
            eventoNotificacao.put("medico_nome", medico != null && medico.getNome() != null ? medico.getNome() : "Médico");
            eventoNotificacao.put("medico_email", medico != null && medico.getEmail() != null ? medico.getEmail() : "");
            // Buscar telefone do médico
            String medicoTelefone = "";
            if (medico != null) {
                try {
                    var telefones = telefoneGateway.buscarPorUsuarioId(medico.getId());
                    if (!telefones.isEmpty()) {
                        medicoTelefone = telefones.get(0).getNumero();
                    }
                } catch (Exception e) {
                    logger.warn("Erro ao buscar telefone do médico {}: {}", medico.getId(), e.getMessage());
                }
            }
            eventoNotificacao.put("medico_telefone", medicoTelefone);
            eventoNotificacao.put("medico_especialidade", medico != null && especialidade.getNome() != null ? especialidade.getNome() : "Especialidade não informada");
            eventoNotificacao.put("nova_data_hora", consulta.getDataHora().toString());
            eventoNotificacao.put("observacoes", consulta.getObservacoes() != null ? consulta.getObservacoes() : "");
            eventoNotificacao.put("status", consulta.getStatus().toString());
            eventoNotificacao.put("alteracoes", alteracoes);
            eventoNotificacao.put("editado_por_id", editadoPorId.toString());
            eventoNotificacao.put("timestamp", LocalDateTime.now().toString());

            String jsonEvento = objectMapper.writeValueAsString(eventoNotificacao);
            rabbitTemplate.convertAndSend(
                    rabbitMQConfig.exchangeConsultas().getName(),
                    rabbitMQConfig.routingKeyNotificacoes(),
                    jsonEvento
            );

            logger.info("Notificação de consulta editada publicada com sucesso: {}", consulta.getId());

        } catch (Exception e) {
            logger.error("Erro ao publicar notificação de consulta editada: {}", consulta.getId(), e);
            throw new RuntimeException("Falha ao publicar notificação de consulta editada", e);
        }
    }
}
