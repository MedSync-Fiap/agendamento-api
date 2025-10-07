package com.medsync.cadastroagendamento.presentation.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medsync.cadastroagendamento.domain.exception.*;
import com.medsync.cadastroagendamento.presentation.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper;
    
    @Value("${spring.profiles.active:prod}")
    private String activeProfile;
    
    private static final String DEV_PROFILE = "dev";
    private static final String LOCAL_PROFILE = "local";
    
    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(new ErrorResponse.ValidationError(
                    error.getField(),
                    error.getDefaultMessage(),
                    error.getRejectedValue()
            ));
        }
        
        ErrorResponse errorResponse = createErrorResponse(
                "VALIDATION_ERROR",
                "Dados de entrada inválidos",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                validationErrors,
                null,
                ex
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.warn("Constraint violation: {}", ex.getMessage());
        
        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            validationErrors.add(new ErrorResponse.ValidationError(
                    violation.getPropertyPath().toString(),
                    violation.getMessage(),
                    violation.getInvalidValue()
            ));
        }
        
        ErrorResponse errorResponse = createErrorResponse(
                "CONSTRAINT_VIOLATION",
                "Violação de restrições de validação",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                validationErrors,
                null,
                ex
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        log.warn("Malformed JSON: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "MALFORMED_JSON",
                "Formato JSON inválido",
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.warn("Type mismatch: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "TYPE_MISMATCH",
                String.format("Parâmetro '%s' deve ser do tipo %s", 
                        ex.getName(), ex.getRequiredType().getSimpleName()),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        log.warn("Missing parameter: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "MISSING_PARAMETER",
                String.format("Parâmetro obrigatório '%s' não fornecido", ex.getParameterName()),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        log.warn("Method not supported: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "METHOD_NOT_SUPPORTED",
                String.format("Método %s não suportado para esta URL", ex.getMethod()),
                request.getRequestURI(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        log.warn("No handler found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "NOT_FOUND",
                "Endpoint não encontrado",
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        log.warn("Authentication error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "AUTHENTICATION_ERROR",
                "Erro de autenticação",
                request.getRequestURI(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {
        
        log.warn("Bad credentials: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "BAD_CREDENTIALS",
                "Credenciais inválidas",
                request.getRequestURI(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        log.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "ACCESS_DENIED",
                "Acesso negado",
                request.getRequestURI(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(
            HttpClientErrorException ex, HttpServletRequest request) {
        
        log.error("HTTP client error: {} - {}", ex.getStatusCode(), ex.getMessage());
        
        String message = "Erro na comunicação com serviço externo";
        String details = null;
        
        // Tentar extrair informações do GraphQL se for erro 401
        if (ex.getStatusCode().value() == 401) {
            message = "Erro de autenticação na comunicação com serviço de histórico";
        } else if (ex.getStatusCode().value() == 404) {
            message = "Recurso não encontrado no serviço de histórico";
        }
        
        if (isDevProfile()) {
            try {
                // Tentar parsear a resposta como JSON para extrair detalhes do GraphQL
                String responseBody = ex.getResponseBodyAsString();
                if (responseBody != null && responseBody.contains("errors")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> errorResponse = objectMapper.readValue(responseBody, Map.class);
                    details = errorResponse.toString();
                } else {
                    details = responseBody;
                }
            } catch (Exception e) {
                details = ex.getResponseBodyAsString();
            }
        }
        
        ErrorResponse errorResponse = createErrorResponse(
                "EXTERNAL_SERVICE_ERROR",
                message,
                request.getRequestURI(),
                ex.getStatusCode().value(),
                ex.getStatusText(),
                null,
                details,
                ex
        );
        
        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(
            HttpServerErrorException ex, HttpServletRequest request) {
        
        log.error("HTTP server error: {} - {}", ex.getStatusCode(), ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "EXTERNAL_SERVICE_ERROR",
                "Erro interno no serviço externo",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null,
                isDevProfile() ? ex.getResponseBodyAsString() : null,
                ex
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(
            ResourceAccessException ex, HttpServletRequest request) {
        
        log.error("Resource access error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                "SERVICE_UNAVAILABLE",
                "Serviço temporariamente indisponível",
                request.getRequestURI(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Service Unavailable",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    // ===== EXCEÇÕES PERSONALIZADAS DE INFRAESTRUTURA =====
    
    @ExceptionHandler(GraphQLCommunicationException.class)
    public ResponseEntity<ErrorResponse> handleGraphQLCommunicationException(
            GraphQLCommunicationException ex, HttpServletRequest request) {
        
        log.error("GraphQL communication error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_GATEWAY.value(),
                "Bad Gateway",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }
    
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            DatabaseException ex, HttpServletRequest request) {
        
        log.error("Database error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                "Erro de acesso ao banco de dados",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(
            ExternalServiceException ex, HttpServletRequest request) {
        
        log.error("External service error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                "Erro na comunicação com serviço externo",
                request.getRequestURI(),
                HttpStatus.BAD_GATEWAY.value(),
                "Bad Gateway",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
    }
    
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(
            JwtException ex, HttpServletRequest request) {
        
        log.error("JWT error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                "Erro de autenticação",
                request.getRequestURI(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    // ===== EXCEÇÕES PERSONALIZADAS DE REGRA DE NEGÓCIO =====
    
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNotFoundException(
            UsuarioNotFoundException ex, HttpServletRequest request) {
        
        log.warn("User not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(EspecialidadeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEspecialidadeNotFoundException(
            EspecialidadeNotFoundException ex, HttpServletRequest request) {
        
        log.warn("Specialty not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(UsuarioAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioAlreadyExistsException(
            UsuarioAlreadyExistsException ex, HttpServletRequest request) {
        
        log.warn("User already exists: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(PacienteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePacienteNotFoundException(
            PacienteNotFoundException ex, HttpServletRequest request) {
        
        log.warn("Patient not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
            InvalidCredentialsException ex, HttpServletRequest request) {
        
        log.warn("Invalid credentials: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    @ExceptionHandler(InsufficientPermissionsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPermissionsException(
            InsufficientPermissionsException ex, HttpServletRequest request) {
        
        log.warn("Insufficient permissions: {}", ex.getMessage());
        
        ErrorResponse errorResponse = createErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    // ===== EXCEÇÕES GENÉRICAS (FALLBACK) =====
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        
        String message = "Erro interno do servidor";
        String code = "INTERNAL_ERROR";
        
        // Tratar exceções específicas do GraphQL
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("GraphQL")) {
                code = "GRAPHQL_ERROR";
                message = "Erro na comunicação com serviço de histórico";
            } else if (ex.getMessage().contains("JWT")) {
                code = "JWT_ERROR";
                message = "Erro de autenticação";
            } else if (ex.getMessage().contains("Database")) {
                code = "DATABASE_ERROR";
                message = "Erro de acesso ao banco de dados";
            }
        }
        
        ErrorResponse errorResponse = createErrorResponse(
                code,
                message,
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = createErrorResponse(
                "UNEXPECTED_ERROR",
                "Erro inesperado do servidor",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                null,
                ex.getMessage(),
                ex
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private boolean isDevProfile() {
        return DEV_PROFILE.equals(activeProfile) || LOCAL_PROFILE.equals(activeProfile);
    }

    private String getStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
    
    private ErrorResponse createErrorResponse(String code, String message, String path, int status, String error, 
                                           List<ErrorResponse.ValidationError> validationErrors, String details, Exception ex) {
        return new ErrorResponse(
                code,
                message,
                LocalDateTime.now().toString(),
                path,
                status,
                error,
                validationErrors,
                isDevProfile() ? getStackTrace(ex) : null,
                isDevProfile() ? details : null
        );
    }
}
