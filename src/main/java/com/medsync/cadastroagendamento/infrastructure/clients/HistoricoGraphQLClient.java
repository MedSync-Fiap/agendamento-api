package com.medsync.cadastroagendamento.infrastructure.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
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
import java.util.Optional;
import java.util.UUID;

@Component
public class HistoricoGraphQLClient {

    private static final Logger logger = LoggerFactory.getLogger(HistoricoGraphQLClient.class);
    
    private final RestTemplate restTemplate;
    private final UsuarioGateway usuarioGateway;
    private final EspecialidadeGateway especialidadeGateway;
    private final ObjectMapper objectMapper;
    
    @Value("${app.historico.graphql.url:http://localhost:8081/graphql}")
    private String historicoGraphQLUrl;
    
    public HistoricoGraphQLClient(RestTemplate restTemplate, 
                                 UsuarioGateway usuarioGateway,
                                 EspecialidadeGateway especialidadeGateway) {
        this.restTemplate = restTemplate;
        this.usuarioGateway = usuarioGateway;
        this.especialidadeGateway = especialidadeGateway;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Salva uma nova consulta no histórico
     */
    public void salvarConsulta(UUID consultaId, UUID pacienteId, UUID medicoId, UUID especialidadeId, UUID usuarioId,
                              LocalDateTime dataHora, String observacoes, String status) {
        try {
            // Buscar dados dos usuários
            Usuario paciente = buscarUsuario(pacienteId);
            Usuario medico = buscarUsuario(medicoId);
            Usuario usuario = buscarUsuario(usuarioId);
            
            // Buscar dados da especialidade
            Especialidade especialidade = especialidadeGateway.buscarPorId(especialidadeId)
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada: " + especialidadeId));
            
            // Construir mutation GraphQL
            String mutation = buildSalvarHistoricoMutation(
                consultaId, dataHora, status, observacoes,
                paciente, medico, usuario, especialidade
            );
            
            // Executar mutation
            Map<String, Object> response = executeGraphQLMutation(mutation);
            
            // Verificar se houve erros na resposta
            if (response.containsKey("errors")) {
                logger.error("Erro do GraphQL: {}", response.get("errors"));
                throw new RuntimeException("Erro ao salvar consulta no histórico: " + response.get("errors"));
            }
            
            logger.info("Consulta {} salva no histórico com sucesso", consultaId);
            
        } catch (Exception e) {
            logger.error("Erro ao salvar consulta {} no histórico", consultaId, e);
            throw new RuntimeException("Falha ao salvar consulta no histórico", e);
        }
    }
    
    /**
     * Atualiza uma consulta existente no histórico
     */
    public void atualizarConsulta(UUID consultaId, UUID pacienteId, UUID medicoId, UUID especialidadeId, UUID usuarioId,
                                  LocalDateTime dataHora, String observacoes, String status) {
        try {
            // Buscar dados dos usuários
            Usuario paciente = buscarUsuario(pacienteId);
            Usuario medico = buscarUsuario(medicoId);
            Usuario usuario = buscarUsuario(usuarioId);
            
            // Buscar dados da especialidade
            Especialidade especialidade = especialidadeGateway.buscarPorId(especialidadeId)
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada: " + especialidadeId));
            
            // Construir mutation GraphQL
            String mutation = buildAtualizarHistoricoMutation(
                consultaId, dataHora, status, observacoes,
                paciente, medico, usuario, especialidade
            );
            
            // Executar mutation
            Map<String, Object> response = executeGraphQLMutation(mutation);
            
            // Verificar se houve erros na resposta
            if (response.containsKey("errors")) {
                logger.error("Erro do GraphQL: {}", response.get("errors"));
                throw new RuntimeException("Erro ao atualizar consulta no histórico: " + response.get("errors"));
            }
            
            logger.info("Consulta {} atualizada no histórico com sucesso", consultaId);
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar consulta {} no histórico", consultaId, e);
            throw new RuntimeException("Falha ao atualizar consulta no histórico", e);
        }
    }
    
    private Usuario buscarUsuario(UUID usuarioId) {
        return usuarioGateway.buscarPorId(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + usuarioId));
    }
    
    private String buildSalvarHistoricoMutation(UUID consultaId, LocalDateTime dataHora, String status, String observacoes,
                                               Usuario paciente, Usuario medico, Usuario usuario, Especialidade especialidade) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        return String.format("""
            mutation SalvarHistorico {
                salvarHistorico(input: {
                    consultaId: "%s"
                    dataHora: "%s"
                    status: "%s"
                    observacoes: "%s"
                    tipoEvento: "CRIADA"
                    timestamp: "%s"
                    pacienteId: "%s"
                    pacienteNome: "%s"
                    pacienteCpf: "%s"
                    pacienteEmail: "%s"
                    pacienteDataNascimento: "%s"
                    medicoId: "%s"
                    medicoNome: "%s"
                    medicoCpf: "%s"
                    medicoEmail: "%s"
                    medicoEspecialidade: "%s"
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
            paciente.getId(),
            paciente.getNome(),
            paciente.getCpf(),
            paciente.getEmail(),
            paciente.getDataNascimento().format(DateTimeFormatter.ISO_LOCAL_DATE),
            medico.getId(),
            medico.getNome(),
            medico.getCpf(),
            medico.getEmail(),
            especialidade.getNome(),
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().getTipo().name()
        );
    }
    
    private String buildAtualizarHistoricoMutation(UUID consultaId, LocalDateTime dataHora, String status, String observacoes,
                                                  Usuario paciente, Usuario medico, Usuario usuario, Especialidade especialidade) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        
        return String.format("""
            mutation AtualizarHistorico {
                atualizarHistorico(input: {
                    consultaId: "%s"
                    dataHora: "%s"
                    status: "%s"
                    observacoes: "%s"
                    tipoEvento: "EDITADA"
                    timestamp: "%s"
                    pacienteId: "%s"
                    pacienteNome: "%s"
                    pacienteCpf: "%s"
                    pacienteEmail: "%s"
                    pacienteDataNascimento: "%s"
                    medicoId: "%s"
                    medicoNome: "%s"
                    medicoCpf: "%s"
                    medicoEmail: "%s"
                    medicoEspecialidade: "%s"
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
            paciente.getId(),
            paciente.getNome(),
            paciente.getCpf(),
            paciente.getEmail(),
            paciente.getDataNascimento().format(DateTimeFormatter.ISO_LOCAL_DATE),
            medico.getId(),
            medico.getNome(),
            medico.getCpf(),
            medico.getEmail(),
            especialidade.getNome(),
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getRole().getTipo().name()
        );
    }
    
    private Map<String, Object> executeGraphQLMutation(String mutation) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("query", mutation);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            String response = restTemplate.postForObject(historicoGraphQLUrl, request, String.class);
            
            logger.debug("Resposta do GraphQL: {}", response);
            
            // Parse da resposta JSON
            return objectMapper.readValue(response, Map.class);
            
        } catch (Exception e) {
            logger.error("Erro ao executar mutation GraphQL", e);
            throw new RuntimeException("Falha na comunicação com serviço de histórico", e);
        }
    }
}