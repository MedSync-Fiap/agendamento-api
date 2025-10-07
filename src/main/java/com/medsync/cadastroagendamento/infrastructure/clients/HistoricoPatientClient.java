package com.medsync.cadastroagendamento.infrastructure.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medsync.cadastroagendamento.application.dto.M2MJwt;
import com.medsync.cadastroagendamento.application.service.M2MJwtGeneratorService;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarPacienteRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarPacienteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class HistoricoPatientClient {

    private static final Logger logger = LoggerFactory.getLogger(HistoricoPatientClient.class);
    
    private final RestTemplate restTemplate;
    private final M2MJwtGeneratorService jwtGeneratorService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${app.historico.graphql.url:http://localhost:8081/graphql}")
    private String historicoGraphQLUrl;
    
    public HistoricoPatientClient(RestTemplate restTemplate, M2MJwtGeneratorService jwtGeneratorService) {
        this.restTemplate = restTemplate;
        this.jwtGeneratorService = jwtGeneratorService;
    }
    
    /**
     * Busca dados de um paciente no histórico
     */
    public Map<String, Object> buscarPaciente(UUID pacienteId) {
        try {
            String query = buildBuscarPacienteQuery(pacienteId);
            return executeGraphQLQuery(query);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar paciente {} no histórico", pacienteId, e);
            throw new RuntimeException("Falha ao buscar dados do paciente no histórico", e);
        }
    }
    
    /**
     * Busca histórico médico completo de um paciente
     */
    public Map<String, Object> buscarHistoricoCompleto(UUID pacienteId, Boolean somenteDatasFuturas) {
        try {
            String query = buildBuscarHistoricoCompletoQuery(pacienteId, somenteDatasFuturas);
            return executeGraphQLQuery(query);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar histórico completo do paciente {}", pacienteId, e);
            throw new RuntimeException("Falha ao buscar histórico médico do paciente", e);
        }
    }
    
    /**
     * Busca uma consulta específica no histórico
     */
    public Map<String, Object> buscarConsulta(UUID consultaId, UUID pacienteId) {
        try {
            String query = buildBuscarConsultaQuery(consultaId, pacienteId);
            return executeGraphQLQuery(query);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar consulta {} no histórico", consultaId, e);
            throw new RuntimeException("Falha ao buscar consulta no histórico", e);
        }
    }
    
    /**
     * Cria um novo paciente no histórico
     */
    public Map<String, Object> criarPaciente(CriarPacienteRequest request) {
        try {
            String mutation = buildCriarPacienteMutation(request);
            return executeGraphQLMutation(mutation);
            
        } catch (Exception e) {
            logger.error("Erro ao criar paciente no histórico", e);
            throw new RuntimeException("Falha ao criar paciente no histórico", e);
        }
    }
    
    /**
     * Atualiza dados de um paciente no histórico
     */
    public Map<String, Object> atualizarPaciente(UUID pacienteId, AtualizarPacienteRequest request) {
        try {
            String mutation = buildAtualizarPacienteMutation(pacienteId, request);
            return executeGraphQLMutation(mutation);
            
        } catch (Exception e) {
            logger.error("Erro ao atualizar paciente {} no histórico", pacienteId, e);
            throw new RuntimeException("Falha ao atualizar paciente no histórico", e);
        }
    }
    
    /**
     * Exclui um paciente do histórico (soft delete)
     */
    public void excluirPaciente(UUID pacienteId) {
        try {
            String mutation = buildExcluirPacienteMutation(pacienteId);
            executeGraphQLMutation(mutation);
            
        } catch (Exception e) {
            logger.error("Erro ao excluir paciente {} no histórico", pacienteId, e);
            throw new RuntimeException("Falha ao excluir paciente no histórico", e);
        }
    }
    
    private String buildBuscarPacienteQuery(UUID pacienteId) {
        return String.format("""
            query {
                getMedicalHistoryByPatientId(patientId: "%s") {
                    patient {
                        id
                        name
                        cpf
                        email
                        dateOfBirth
                    }
                }
            }
            """, pacienteId);
    }
    
    private String buildBuscarHistoricoCompletoQuery(UUID pacienteId, boolean somenteDatasFuturas) {
        return String.format("""
            query {
                getMedicalHistoryByPatientId(patientId: "%s") {
                    patient {
                        id
                        name
                        cpf
                        email
                        dateOfBirth
                    }
                    appointments(filter: {onlyFuture: %s}) {
                        id
                        appointmentDateTime
                        status
                        doctor {
                            id
                            name
                            specialty
                        }
                        actionLogs {
                            actionType
                            timestamp
                            user {
                                name
                                role
                            }
                        }
                    }
                }
            }
            """, pacienteId, somenteDatasFuturas);
    }
    
    private String buildBuscarConsultaQuery(UUID consultaId, UUID pacienteId) {
        return String.format("""
            query {
                getAppointmentById(appointmentId: "%s", patientId: "%s") {
                    id
                    appointmentDateTime
                    status
                    doctor {
                        id
                        name
                        specialty
                    }
                }
            }
            """, consultaId, pacienteId);
    }
    
    private String buildCriarPacienteMutation(CriarPacienteRequest request) {
        return String.format("""
            mutation CriarPaciente {
                criarPaciente(input: {
                    nome: "%s"
                    email: "%s"
                    cpf: "%s"
                    dataNascimento: "%s"
                    observacoes: "%s"
                }) {
                    id
                    nome
                    email
                    cpf
                    dataNascimento
                }
            }
            """,
            request.nome(),
            request.email(),
            request.cpf(),
            request.dataNascimento().format(DateTimeFormatter.ISO_LOCAL_DATE),
            request.observacoes() != null ? request.observacoes() : ""
        );
    }
    
    private String buildAtualizarPacienteMutation(UUID pacienteId, AtualizarPacienteRequest request) {
        return String.format("""
            mutation AtualizarPaciente {
                atualizarPaciente(input: {
                    id: "%s"
                    nome: "%s"
                    email: "%s"
                    cpf: "%s"
                    dataNascimento: "%s"
                    observacoes: "%s"
                }) {
                    id
                    nome
                    email
                    cpf
                    dataNascimento
                }
            }
            """,
            pacienteId,
            request.nome() != null ? request.nome() : "",
            request.email() != null ? request.email() : "",
            request.cpf() != null ? request.cpf() : "",
            request.dataNascimento() != null ? request.dataNascimento().format(DateTimeFormatter.ISO_LOCAL_DATE) : "",
            request.observacoes() != null ? request.observacoes() : ""
        );
    }
    
    private String buildExcluirPacienteMutation(UUID pacienteId) {
        return String.format("""
            mutation ExcluirPaciente {
                excluirPaciente(id: "%s") {
                    id
                    ativo
                }
            }
            """, pacienteId);
    }
    
    private Map<String, Object> executeGraphQLQuery(String query) {
        try {
            M2MJwt jwt = jwtGeneratorService.getTokenHistorico();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(jwt.token());
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("query", query);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            String response = restTemplate.postForObject(historicoGraphQLUrl, request, String.class);
            
            logger.debug("Resposta do GraphQL: {}", response);
            
            // Parse da resposta JSON
            return objectMapper.readValue(response, Map.class);
            
        } catch (Exception e) {
            logger.error("Erro ao executar query GraphQL", e);
            throw new RuntimeException("Falha na comunicação com serviço de histórico", e);
        }
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
            Map<String, Object> result = objectMapper.readValue(response, Map.class);
            
            // Verificar se houve erros na resposta
            if (result.containsKey("errors")) {
                logger.error("Erro do GraphQL: {}", result.get("errors"));
                throw new RuntimeException("Erro ao executar mutation no histórico: " + result.get("errors"));
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Erro ao executar mutation GraphQL", e);
            throw new RuntimeException("Falha na comunicação com serviço de histórico", e);
        }
    }
}
