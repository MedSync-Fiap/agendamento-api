package com.medsync.cadastroagendamento.presentation.handler;

import com.medsync.cadastroagendamento.application.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        ErrorResponse error = new ErrorResponse(
            "USUARIO_NAO_ENCONTRADO",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    
    @ExceptionHandler(EmailJaExisteException.class)
    public ResponseEntity<ErrorResponse> handleEmailJaExiste(EmailJaExisteException ex) {
        ErrorResponse error = new ErrorResponse(
            "EMAIL_JA_EXISTE",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(CpfJaExisteException.class)
    public ResponseEntity<ErrorResponse> handleCpfJaExiste(CpfJaExisteException ex) {
        ErrorResponse error = new ErrorResponse(
            "CPF_JA_EXISTE",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    
    
    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErrorResponse> handleCredenciaisInvalidas(CredenciaisInvalidasException ex) {
        ErrorResponse error = new ErrorResponse(
            "CREDENCIAIS_INVALIDAS",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse error = new ValidationErrorResponse(
            "VALIDATION_ERROR",
            "Erro de validação nos dados fornecidos",
            LocalDateTime.now(),
            errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        
        // Verificar se é erro de parsing de data
        if (message != null && message.contains("LocalDateTime")) {
            message = "Formato de data inválido. Use o formato: yyyy-MM-ddTHH:mm:ss (ex: 2025-12-17T14:30:00)";
        } else if (message != null && message.contains("JSON parse error")) {
            message = "Erro ao processar JSON. Verifique o formato dos dados enviados.";
        } else {
            message = "Erro ao processar os dados da requisição: " + message;
        }
        
        ErrorResponse error = new ErrorResponse(
            "INVALID_REQUEST_FORMAT",
            message,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Log the full stack trace for debugging
        ex.printStackTrace();
        
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Erro interno do servidor: " + ex.getMessage() + " - " + ex.getClass().getSimpleName(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
    public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp
    ) {}
    
    public record ValidationErrorResponse(
        String code,
        String message,
        LocalDateTime timestamp,
        Map<String, String> errors
    ) {}
}
