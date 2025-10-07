package com.medsync.cadastroagendamento.domain.exception;

public abstract class InfrastructureException extends RuntimeException {
    
    private final String errorCode;
    private final Object[] parameters;
    
    protected InfrastructureException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }
    
    protected InfrastructureException(String message, String errorCode, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }
    
    protected InfrastructureException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }
    
    protected InfrastructureException(String message, Throwable cause, String errorCode, Object... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Object[] getParameters() {
        return parameters;
    }
}
