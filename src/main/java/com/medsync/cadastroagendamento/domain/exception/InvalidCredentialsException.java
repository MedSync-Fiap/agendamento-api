package com.medsync.cadastroagendamento.domain.exception;

public class InvalidCredentialsException extends BusinessRuleException {
    
    public static final String ERROR_CODE = "INVALID_CREDENTIALS";
    
    public InvalidCredentialsException(String message) {
        super(message, ERROR_CODE);
    }
    
    public InvalidCredentialsException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public static InvalidCredentialsException invalidPassword() {
        return new InvalidCredentialsException("Senha inválida");
    }
    
    public static InvalidCredentialsException invalidEmail() {
        return new InvalidCredentialsException("Email inválido");
    }
}
