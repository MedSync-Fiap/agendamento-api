package com.medsync.cadastroagendamento.infrastructure.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medsync.cadastroagendamento.application.dto.M2MJwt;
import com.medsync.cadastroagendamento.application.service.M2MJwtGeneratorService;
import com.medsync.cadastroagendamento.domain.exception.GraphQLCommunicationException;
import com.medsync.cadastroagendamento.domain.exception.JwtException;
import com.medsync.cadastroagendamento.domain.exception.PacienteAlreadyExistsException;
import com.medsync.cadastroagendamento.domain.exception.PacienteNotFoundException;
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
    
    private final ObjectMapper objectMapper;
    
    @Value("${app.historico.graphql.url:http://localhost:8081/graphql}")
    private String historicoGraphQLUrl;
    
    public HistoricoPatientClient(RestTemplate restTemplate, M2MJwtGeneratorService jwtGeneratorService, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.jwtGeneratorService = jwtGeneratorService;
        this.objectMapper = objectMapper;
    }
    
    public Map<String, Object> buscarPacientePorCpf(String cpf) {
        try {
            String query = buildBuscarPacientePorCpfQuery(cpf);
            return executeGraphQLQuery(query);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar paciente por CPF {} no histórico", cpf, e);
            throw new PacienteNotFoundException("Paciente não encontrado com CPF: " + cpf, cpf);
        }
    }

    private boolean existeClientePorCpf(String cpf) {
        try {
            Map<String, Object> response = buscarPacientePorCpf(cpf);
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean existeClientePorId(UUID pacienteId) {
        try {
            Map<String, Object> response = buscarPaciente(pacienteId);
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    private void validarDadosAtualizacao(AtualizarPacienteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dados de atualização não podem ser nulos");
        }
        
        // Validar se pelo menos um campo foi fornecido para atualização
        if (request.nome() == null && request.email() == null && 
            request.cpf() == null && request.dataNascimento() == null && 
            request.observacoes() == null) {
            throw new IllegalArgumentException("Pelo menos um campo deve ser fornecido para atualização");
        }
        
        // Validar formato do email se fornecido
        if (request.email() != null && !request.email().isEmpty()) {
            if (!request.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Formato de email inválido");
            }
        }
        
        // Validar formato do CPF se fornecido
        if (request.cpf() != null && !request.cpf().isEmpty()) {
            if (!request.cpf().matches("\\d{11}")) {
                throw new IllegalArgumentException("CPF deve conter exatamente 11 dígitos");
            }
        }
        
        // Validar nome se fornecido
        if (request.nome() != null && !request.nome().isEmpty()) {
            if (request.nome().length() < 2 || request.nome().length() > 100) {
                throw new IllegalArgumentException("Nome deve ter entre 2 e 100 caracteres");
            }
        }
        
        // Validar observações se fornecidas
        if (request.observacoes() != null && request.observacoes().length() > 500) {
            throw new IllegalArgumentException("Observações devem ter no máximo 500 caracteres");
        }
    }
    
    public Map<String, Object> criarPaciente(CriarPacienteRequest request) {
        try {
            if (existeClientePorCpf(request.cpf())) {
                throw PacienteAlreadyExistsException.byCpf(request.cpf());
            }
            String mutation = buildCriarPacienteMutation(request);
            return executeGraphQLMutation(mutation);
            
        }  catch (PacienteAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao criar paciente no histórico", e);
            throw new GraphQLCommunicationException("Falha ao criar paciente no histórico", e);
        }
    }
    
    public Map<String, Object> buscarPaciente(UUID pacienteId) {
        try {
            String query = buildBuscarPacienteQuery(pacienteId);
            return executeGraphQLQuery(query);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar paciente {} no histórico", pacienteId, e);
            throw new PacienteNotFoundException("Paciente não encontrado com ID: " + pacienteId, pacienteId);
        }
    }
    
    public Map<String, Object> buscarHistoricoCompleto(UUID pacienteId) {
        try {
            String query = buildBuscarHistoricoCompletoQuery(pacienteId);
            return executeGraphQLQuery(query);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar histórico completo do paciente {}", pacienteId, e);
            throw new PacienteNotFoundException("Paciente não encontrado com ID: " + pacienteId, pacienteId);
        }
    }
    
    public Map<String, Object> buscarConsulta(UUID consultaId, UUID pacienteId) {
        try {
            String query = buildBuscarConsultaQuery(consultaId, pacienteId);
            return executeGraphQLQuery(query);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar consulta {} no histórico", consultaId, e);
            throw new PacienteNotFoundException("Consulta não encontrada para o paciente: " + pacienteId, pacienteId);
        }
    }
    
    
    public Map<String, Object> atualizarPaciente(UUID pacienteId, AtualizarPacienteRequest request) {
        try {
            // Validar dados do request
            validarDadosAtualizacao(request);
            
            String mutation = buildAtualizarPacienteMutation(pacienteId, request);
            return executeGraphQLMutation(mutation);
            
        } catch (GraphQLCommunicationException e) {
            // Verificar se o erro é de paciente não encontrado
            if (e.getMessage().contains("Paciente não encontrado") || e.getMessage().contains("not found")) {
                throw new PacienteNotFoundException("Paciente não encontrado com ID: " + pacienteId, pacienteId);
            }
            throw e; // Re-lança exceção específica de comunicação
        } catch (Exception e) {
            logger.error("Erro ao atualizar paciente {} no histórico", pacienteId, e);
            throw new GraphQLCommunicationException("Falha ao atualizar paciente no histórico", e);
        }
    }
    
    public void excluirPaciente(UUID pacienteId) {
        try {
            String mutation = buildExcluirPacienteMutation(pacienteId);
            executeGraphQLMutation(mutation);
            
        } catch (GraphQLCommunicationException e) {
            throw e; // Re-lança exceção específica de comunicação
        } catch (Exception e) {
            logger.error("Erro ao excluir paciente {} no histórico", pacienteId, e);
            throw new GraphQLCommunicationException("Falha ao excluir paciente no histórico", e);
        }
    }
    
    public Map<String, Object> inativarPaciente(UUID pacienteId) {
        try {
            String mutation = buildInativarPacienteMutation(pacienteId);
            return executeGraphQLMutation(mutation);

        } catch (GraphQLCommunicationException e) {
            throw e; // Re-lança exceção específica de comunicação
        } catch (Exception e) {
            logger.error("Erro ao inativar paciente {} no histórico", pacienteId, e);
            throw new GraphQLCommunicationException("Falha ao inativar paciente no histórico", e);
        }
    }
    
    public Map<String, Object> reativarPaciente(UUID pacienteId) {
        try {
            String mutation = buildReativarPacienteMutation(pacienteId);
            return executeGraphQLMutation(mutation);

        } catch (GraphQLCommunicationException e) {
            throw e; // Re-lança exceção específica de comunicação
        } catch (Exception e) {
            logger.error("Erro ao reativar paciente {} no histórico", pacienteId, e);
            throw new GraphQLCommunicationException("Falha ao reativar paciente no histórico", e);
        }
    }
    
    private String buildBuscarPacientePorCpfQuery(String cpf) {
        return String.format("""
            query {
                getMedicalHistoryByPatientCpf(patientCpf: "%s") {
                    patient {
                        id
                        name
                        cpf
                        email
                        dateOfBirth
                    }
                }
            }
            """, cpf);
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
    
    private String buildBuscarHistoricoCompletoQuery(UUID pacienteId) {
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
                    appointments {
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
            """, pacienteId);
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
    
    
    private String buildAtualizarPacienteMutation(UUID pacienteId, AtualizarPacienteRequest request) {
        // Nova mutation específica para atualizar dados do paciente
        return String.format("""
            mutation AtualizarPaciente {
                updatePatient(patientId: "%s", patientInput: {
                    nome: "%s"
                    cpf: "%s"
                    email: "%s"
                    dataNascimento: "%s"
                    observacoes: "%s"
                }) {
                    id
                    nome
                    cpf
                    email
                    dataNascimento
                    observacoes
                    ativo
                    atualizadoEm
                }
            }
            """,
            pacienteId.toString(),
            request.nome() != null ? request.nome() : "",
            request.cpf() != null ? request.cpf() : "",
            request.email() != null ? request.email() : "",
            request.dataNascimento() != null ? request.dataNascimento().format(DateTimeFormatter.ISO_LOCAL_DATE) : "",
            request.observacoes() != null ? request.observacoes() : ""
        );
    }
    
    private String buildExcluirPacienteMutation(UUID pacienteId) {
        // A API de histórico não tem mutation específica para excluir pacientes
        // Vamos usar updateAppointment para marcar como cancelado
        String consultaId = UUID.randomUUID().toString();
        String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        return String.format("""
            mutation UpdateAppointment {
                updateAppointment(updateAppointmentInput: {
                    consultaId: "%s"
                    dataHora: "%s"
                    status: "CANCELADA"
                    observacoes: "Paciente removido do sistema"
                    tipoEvento: "CANCELLATION"
                    timestamp: "%s"
                    
                    # Dados do paciente
                    pacienteId: "%s"
                    pacienteNome: "Paciente Removido"
                    pacienteCpf: "00000000000"
                    pacienteEmail: "removido@medsync.com"
                    pacienteDataNascimento: "1900-01-01"
                    
                    # Dados do médico (dados fictícios para cancelamento)
                    medicoId: "00000000-0000-0000-0000-000000000000"
                    medicoNome: "Sistema"
                    medicoCpf: "00000000000"
                    medicoEmail: "sistema@medsync.com"
                    medicoEspecialidade: "Sistema"
                    
                    # Dados do usuário (dados fictícios para cancelamento)
                    usuarioId: "00000000-0000-0000-0000-000000000000"
                    usuarioNome: "Sistema"
                    usuarioEmail: "sistema@medsync.com"
                    usuarioRole: "SISTEMA"
                }) {
                    patient {
                        id
                        name
                    }
                }
            }
            """,
            consultaId,
            java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            timestamp,
            pacienteId.toString()
        );
    }
    
    private String buildInativarPacienteMutation(UUID pacienteId) {
        String consultaId = UUID.randomUUID().toString();
        String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return String.format("""
            mutation UpdateAppointment {
                updateAppointment(updateAppointmentInput: {
                    consultaId: "%s"
                    dataHora: "%s"
                    status: "INATIVO"
                    observacoes: "Paciente inativado no sistema"
                    tipoEvento: "INACTIVATION"
                    timestamp: "%s"

                    # Dados do paciente
                    pacienteId: "%s"
                    pacienteNome: "Paciente Inativo"
                    pacienteCpf: "00000000000"
                    pacienteEmail: "inativo@medsync.com"
                    pacienteDataNascimento: "1900-01-01"

                    # Dados do médico (dados fictícios para inativação)
                    medicoId: "00000000-0000-0000-0000-000000000000"
                    medicoNome: "Sistema"
                    medicoCpf: "00000000000"
                    medicoEmail: "sistema@medsync.com"
                    medicoEspecialidade: "Sistema"

                    # Dados da especialidade (obrigatórios)
                    especialidadeId: "00000000-0000-0000-0000-000000000000"
                    especialidadeNome: "Sistema"

                    # Dados do usuário (dados fictícios para inativação)
                    usuarioId: "00000000-0000-0000-0000-000000000000"
                    usuarioNome: "Sistema"
                    usuarioEmail: "sistema@medsync.com"
                    usuarioRole: "SISTEMA"
                }) {
                    patient {
                        id
                        name
                        status
                    }
                }
            }
            """,
            consultaId,
            java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            timestamp,
            pacienteId.toString()
        );
    }
    
    private String buildReativarPacienteMutation(UUID pacienteId) {
        String consultaId = UUID.randomUUID().toString();
        String timestamp = java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return String.format("""
            mutation UpdateAppointment {
                updateAppointment(updateAppointmentInput: {
                    consultaId: "%s"
                    dataHora: "%s"
                    status: "ATIVO"
                    observacoes: "Paciente reativado no sistema"
                    tipoEvento: "REACTIVATION"
                    timestamp: "%s"

                    # Dados do paciente
                    pacienteId: "%s"
                    pacienteNome: "Paciente Reativado"
                    pacienteCpf: "00000000000"
                    pacienteEmail: "reativado@medsync.com"
                    pacienteDataNascimento: "1900-01-01"

                    # Dados do médico (dados fictícios para reativação)
                    medicoId: "00000000-0000-0000-0000-000000000000"
                    medicoNome: "Sistema"
                    medicoCpf: "00000000000"
                    medicoEmail: "sistema@medsync.com"
                    medicoEspecialidade: "Sistema"

                    # Dados da especialidade (obrigatórios)
                    especialidadeId: "00000000-0000-0000-0000-000000000000"
                    especialidadeNome: "Sistema"

                    # Dados do usuário (dados fictícios para reativação)
                    usuarioId: "00000000-0000-0000-0000-000000000000"
                    usuarioNome: "Sistema"
                    usuarioEmail: "sistema@medsync.com"
                    usuarioRole: "SISTEMA"
                }) {
                    patient {
                        id
                        name
                        status
                    }
                }
            }
            """,
            consultaId,
            java.time.LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            timestamp,
            pacienteId.toString()
        );
    }
    
    private String buildCriarPacienteMutation(CriarPacienteRequest request) {
        return String.format("""
            mutation CriarPaciente {
                createPatient(patientInput: {
                    nome: "%s"
                    cpf: "%s"
                    email: "%s"
                    dataNascimento: "%s"
                    observacoes: "%s"
                }) {
                    id
                    nome
                    cpf
                    email
                    dataNascimento
                    observacoes
                    ativo
                    criadoEm
                }
            }
            """,
            request.nome(),
            request.cpf(),
            request.email(),
            request.dataNascimento().format(DateTimeFormatter.ISO_LOCAL_DATE),
            request.observacoes() != null ? request.observacoes() : ""
        );
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
            
            Map<String, Object> fullResponse = objectMapper.readValue(response, Map.class);
            
            if (fullResponse.containsKey("errors")) {
                logger.error("Erro do GraphQL: {}", fullResponse.get("errors"));
                throw new GraphQLCommunicationException("Erro ao executar query no histórico: " + fullResponse.get("errors"));
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) fullResponse.get("data");
            if (data == null) {
                throw new GraphQLCommunicationException("Resposta GraphQL não contém dados");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) data.values().iterator().next();
            
            // Para queries de histórico médico, retornar o objeto completo (patient + appointments)
            // Para outras queries, extrair apenas o patient se existir wrapper
            if (result.containsKey("patient") && result.containsKey("appointments")) {
                // É uma resposta de histórico médico completo - retornar tudo
                return result;
            } else if (result.containsKey("patient")) {
                // É uma resposta com wrapper patient - extrair apenas o patient
                @SuppressWarnings("unchecked")
                Map<String, Object> patientData = (Map<String, Object>) result.get("patient");
                return patientData;
            }
            
            return result;
            
        } catch (GraphQLCommunicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao executar query GraphQL", e);
            throw new GraphQLCommunicationException("Falha na comunicação com serviço de histórico", e);
        }
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
            
            Map<String, Object> fullResponse = objectMapper.readValue(response, Map.class);
            
            if (fullResponse.containsKey("errors")) {
                logger.error("Erro do GraphQL: {}", fullResponse.get("errors"));
                throw new GraphQLCommunicationException("Erro ao executar mutation no histórico: " + fullResponse.get("errors"));
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) fullResponse.get("data");
            if (data == null) {
                throw new GraphQLCommunicationException("Resposta GraphQL não contém dados");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) data.values().iterator().next();
            
            // Extrair o conteúdo do wrapper 'patient' se existir
            if (result.containsKey("patient")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> patientData = (Map<String, Object>) result.get("patient");
                return patientData;
            }
            
            return result;
            
        } catch (GraphQLCommunicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao executar mutation GraphQL", e);
            throw new GraphQLCommunicationException("Falha na comunicação com serviço de histórico", e);
        }
    }
}
