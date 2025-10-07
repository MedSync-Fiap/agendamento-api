package com.medsync.cadastroagendamento.domain.exception;

public class ExternalServiceException extends InfrastructureException {
    
    public static final String ERROR_CODE = "EXTERNAL_SERVICE_ERROR";
    
    public ExternalServiceException(String message) {
        super(message, ERROR_CODE);
    }
    
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause, ERROR_CODE);
    }
    
    public ExternalServiceException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public ExternalServiceException(String message, Throwable cause, Object... parameters) {
        super(message, cause, ERROR_CODE, parameters);
    }
}
