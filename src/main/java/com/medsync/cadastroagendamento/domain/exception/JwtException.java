package com.medsync.cadastroagendamento.domain.exception;

public class JwtException extends InfrastructureException {
    
    public static final String ERROR_CODE = "JWT_ERROR";
    
    public JwtException(String message) {
        super(message, ERROR_CODE);
    }
    
    public JwtException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
    
    public JwtException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public JwtException(String message, Throwable cause, Object... parameters) {
        super(message, cause, ERROR_CODE, parameters);
    }
}
