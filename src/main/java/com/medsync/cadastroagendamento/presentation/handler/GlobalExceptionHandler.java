package com.medsync.cadastroagendamento.presentation.handler;

import com.medsync.cadastroagendamento.application.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    
    @ExceptionHandler(ConsultaNaoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleConsultaNaoEncontrada(ConsultaNaoEncontradaException ex) {
        ErrorResponse error = new ErrorResponse(
            "CONSULTA_NAO_ENCONTRADA",
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
    
    @ExceptionHandler(ConflitoHorarioException.class)
    public ResponseEntity<ErrorResponse> handleConflitoHorario(ConflitoHorarioException ex) {
        ErrorResponse error = new ErrorResponse(
            "CONFLITO_HORARIO",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(ConsultaNaoPodeSerEditadaException.class)
    public ResponseEntity<ErrorResponse> handleConsultaNaoPodeSerEditada(ConsultaNaoPodeSerEditadaException ex) {
        ErrorResponse error = new ErrorResponse(
            "CONSULTA_NAO_PODE_SER_EDITADA",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Erro interno do servidor",
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
