package com.medsync.cadastroagendamento.domain.exception;

public class DatabaseException extends InfrastructureException {
    
    public static final String ERROR_CODE = "DATABASE_ERROR";
    
    public DatabaseException(String message) {
        super(message, ERROR_CODE);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
    
    public DatabaseException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public DatabaseException(String message, Throwable cause, Object... parameters) {
        super(message, cause, ERROR_CODE, parameters);
    }
}
