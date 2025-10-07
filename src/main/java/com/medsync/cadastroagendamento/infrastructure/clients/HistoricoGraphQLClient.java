package com.medsync.cadastroagendamento.infrastructure.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medsync.cadastroagendamento.application.dto.M2MJwt;
import com.medsync.cadastroagendamento.application.service.M2MJwtGeneratorService;
import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.exception.EspecialidadeNotFoundException;
import com.medsync.cadastroagendamento.domain.exception.GraphQLCommunicationException;
import com.medsync.cadastroagendamento.domain.exception.PacienteNotFoundException;
import com.medsync.cadastroagendamento.domain.exception.UsuarioNotFoundException;
import com.medsync.cadastroagendamento.domain.gateways.EspecialidadeGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class HistoricoGraphQLClient {

    private static final Logger logger = LoggerFactory.getLogger(HistoricoGraphQLClient.class);
    
    private final RestTemplate restTemplate;
    private final UsuarioGateway usuarioGateway;
    private final EspecialidadeGateway especialidadeGateway;
    private final ObjectMapper objectMapper;
    private final M2MJwtGeneratorService jwtGeneratorService;
    
    @Value("${app.historico.graphql.url:http://localhost:8081/graphql}")
    private String historicoGraphQLUrl;
    
    public HistoricoGraphQLClient(RestTemplate restTemplate, 
                                 UsuarioGateway usuarioGateway,
                                 EspecialidadeGateway especialidadeGateway,
                                 M2MJwtGeneratorService jwtGeneratorService,
                                 ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.usuarioGateway = usuarioGateway;
        this.especialidadeGateway = especialidadeGateway;
        this.jwtGeneratorService = jwtGeneratorService;
        this.objectMapper = objectMapper;
    }
    
    public void salvarConsulta(UUID consultaId, UUID pacienteId, UUID medicoId, UUID especialidadeId, UUID usuarioId,
                              LocalDateTime dataHora, String observacoes, String status) {
        try {
            Usuario medico = usuarioGateway.buscarPorId(medicoId)
                    .orElseThrow(() -> UsuarioNotFoundException.byId(medicoId));
            Usuario usuario = usuarioGateway.buscarPorId(usuarioId)
                    .orElseThrow(() -> UsuarioNotFoundException.byId(usuarioId));
            Especialidade especialidade = especialidadeGateway.buscarPorId(especialidadeId)
                    .orElseThrow(() -> EspecialidadeNotFoundException.byId(especialidadeId));
            
            String mutation = buildSalvarHistoricoMutation(
                consultaId, pacienteId, medico, especialidade, usuario,
                dataHora, status, observacoes
            );
            
            Map<String, Object> response = executeGraphQLMutation(mutation);
            
            if (response.containsKey("errors")) {
                logger.error("Erro do GraphQL: {}", response.get("errors"));
                throw new RuntimeException("Erro ao salvar consulta no histórico: " + response.get("errors"));
            }
            
            logger.info("Consulta {} salva no histórico com sucesso", consultaId);
            
        } catch (UsuarioNotFoundException | EspecialidadeNotFoundException e) {
            logger.error("Erro ao buscar dados para consulta {}: {}", consultaId, e.getMessage());
            throw e; // Re-lança a exceção específica
        } catch (Exception e) {
            logger.error("Erro ao salvar consulta {} no histórico", consultaId, e);
            throw new RuntimeException("Falha ao salvar consulta no histórico", e);
        }
    }
    
    public void atualizarConsulta(UUID consultaId, UUID pacienteId, UUID medicoId, UUID especialidadeId, UUID usuarioId,
                                  LocalDateTime dataHora, String observacoes, String status) {
        try {
            // Buscar dados completos do médico, especialidade e usuário no agendamento-api
            // Apenas o paciente será validado pelo historico-api
            Usuario medico = usuarioGateway.buscarPorId(medicoId)
                    .orElseThrow(() -> UsuarioNotFoundException.byId(medicoId));
            Usuario usuario = usuarioGateway.buscarPorId(usuarioId)
                    .orElseThrow(() -> UsuarioNotFoundException.byId(usuarioId));
            Especialidade especialidade = especialidadeGateway.buscarPorId(especialidadeId)
                    .orElseThrow(() -> EspecialidadeNotFoundException.byId(especialidadeId));
            
            String mutation = buildAtualizarHistoricoMutation(
                consultaId, pacienteId, medico, especialidade, usuario,
                dataHora, status, observacoes
            );
            
            Map<String, Object> response = executeGraphQLMutation(mutation);
            
            if (response.containsKey("errors")) {
                logger.error("Erro do GraphQL: {}", response.get("errors"));
                throw new RuntimeException("Erro ao atualizar consulta no histórico: " + response.get("errors"));
            }
            
            logger.info("Consulta {} atualizada no histórico com sucesso", consultaId);
            
        } catch (UsuarioNotFoundException | EspecialidadeNotFoundException e) {
            logger.error("Erro ao buscar dados para atualização da consulta {}: {}", consultaId, e.getMessage());
            throw e; // Re-lança a exceção específica
        } catch (Exception e) {
            logger.error("Erro ao atualizar consulta {} no histórico", consultaId, e);
            throw new RuntimeException("Falha ao atualizar consulta no histórico", e);
        }
    }
    
    public void atualizarStatusConsulta(UUID consultaId, String status, String observacoes, UUID usuarioId) {
        try {
            // Buscar dados completos do usuário no agendamento-api
            Usuario usuario = usuarioGateway.buscarPorId(usuarioId)
                    .orElseThrow(() -> UsuarioNotFoundException.byId(usuarioId));
            
            String mutation = buildAtualizarStatusMutation(consultaId, status, observacoes, usuario);
            
            Map<String, Object> response = executeGraphQLMutation(mutation);
            
            if (response.containsKey("errors")) {
                logger.error("Erro do GraphQL: {}", response.get("errors"));
                throw new RuntimeException("Erro ao atualizar status da consulta no histórico: " + response.get("errors"));
            }
            
            logger.info("Status da consulta {} atualizado no histórico com sucesso", consultaId);
            
        } catch (UsuarioNotFoundException e) {
            logger.error("Erro ao buscar usuário para atualização de status da consulta {}: {}", consultaId, e.getMessage());
            throw e; // Re-lança a exceção específica
        } catch (Exception e) {
            logger.error("Erro ao atualizar status da consulta {} no histórico", consultaId, e);
            throw new RuntimeException("Falha ao atualizar status da consulta no histórico", e);
        }
    }
    
    
    private String buildSalvarHistoricoMutation(UUID consultaId, UUID pacienteId, Usuario medico, Especialidade especialidade, Usuario usuario,
                                               LocalDateTime dataHora, String status, String observacoes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        return String.format("""
            mutation SalvarHistorico {
                saveNewAppointment(newAppointmentInput: {
                    consultaId: "%s"
                    dataHora: "%s"
                    status: "%s"
                    observacoes: "%s"
                    tipoEvento: "CRIADA"
                    timestamp: "%s"
                    pacienteId: "%s"
                    medicoId: "%s"
                    medicoNome: "%s"
                    medicoCpf: "%s"
                    medicoEmail: "%s"
                    especialidadeId: "%s"
                    especialidadeNome: "%s"
                    usuarioId: "%s"
                    usuarioNome: "%s"
                    usuarioEmail: "%s"
                    usuarioRole: "%s"
                }) {
                    patient {
                        id
                        name
                    }
                    appointments {
                        id
                        status
                    }
                }
            }
            """,
            consultaId,
            dataHora.format(formatter),
            status,
            observacoes != null ? observacoes : "",
            LocalDateTime.now().format(formatter),
            pacienteId,
            medico.getId(),
            medico.getNome(),
            medico.getCpf(),
            medico.getEmail(),
            especialidade.getId(),
            especialidade.getNome(),
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().getTipo().name()
        );
    }
    
    private String buildAtualizarHistoricoMutation(UUID consultaId, UUID pacienteId, Usuario medico, Especialidade especialidade, Usuario usuario,
                                                  LocalDateTime dataHora, String status, String observacoes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        return String.format("""
            mutation AtualizarHistorico {
                updateAppointment(updateAppointmentInput: {
                    consultaId: "%s"
                    dataHora: "%s"
                    status: "%s"
                    observacoes: "%s"
                    tipoEvento: "EDITADA"
                    timestamp: "%s"
                    pacienteId: "%s"
                    medicoId: "%s"
                    medicoNome: "%s"
                    medicoCpf: "%s"
                    medicoEmail: "%s"
                    especialidadeId: "%s"
                    especialidadeNome: "%s"
                    usuarioId: "%s"
                    usuarioNome: "%s"
                    usuarioEmail: "%s"
                    usuarioRole: "%s"
                }) {
                    patient {
                        id
                        name
                    }
                    appointments {
                        id
                        status
                    }
                }
            }
            """,
            consultaId,
            dataHora.format(formatter),
            status,
            observacoes != null ? observacoes : "",
            LocalDateTime.now().format(formatter),
            pacienteId,
            medico.getId(),
            medico.getNome(),
            medico.getCpf(),
            medico.getEmail(),
            especialidade.getId(),
            especialidade.getNome(),
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().getTipo().name()
        );
    }
    
    private String buildAtualizarStatusMutation(UUID consultaId, String status, String observacoes, Usuario usuario) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        return String.format("""
            mutation AtualizarStatus {
                updateAppointment(updateAppointmentInput: {
                    consultaId: "%s"
                    dataHora: "%s"
                    status: "%s"
                    observacoes: "%s"
                    tipoEvento: "STATUS_UPDATE"
                    timestamp: "%s"
                    pacienteId: "00000000-0000-0000-0000-000000000000"
                    medicoId: "00000000-0000-0000-0000-000000000000"
                    medicoNome: "Sistema"
                    medicoCpf: "00000000000"
                    medicoEmail: "sistema@medsync.com"
                    especialidadeId: "00000000-0000-0000-0000-000000000000"
                    especialidadeNome: "Sistema"
                    usuarioId: "%s"
                    usuarioNome: "%s"
                    usuarioEmail: "%s"
                    usuarioRole: "%s"
                }) {
                    patient {
                        id
                        name
                    }
                    appointments {
                        id
                        status
                    }
                }
            }
            """,
            consultaId,
            LocalDateTime.now().format(formatter),
            status,
            observacoes != null ? observacoes : "",
            LocalDateTime.now().format(formatter),
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().getTipo().name()
        );
    }
    
    private Map<String, Object> executeGraphQLMutation(String mutation) {
        try {
            M2MJwt jwt = jwtGeneratorService.getTokenHistorico();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(jwt.token());
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("query", mutation);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            String response = restTemplate.postForObject(historicoGraphQLUrl, request, String.class);
            
            logger.debug("Resposta do GraphQL: {}", response);
            
            return objectMapper.readValue(response, Map.class);
            
        } catch (GraphQLCommunicationException e) {
            throw e; // Re-lança exceção específica de comunicação
        } catch (Exception e) {
            logger.error("Erro ao executar mutation GraphQL", e);
            throw new GraphQLCommunicationException("Falha na comunicação com serviço de histórico", e);
        }
    }
}