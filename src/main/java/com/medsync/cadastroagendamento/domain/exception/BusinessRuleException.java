package com.medsync.cadastroagendamento.domain.exception;

public abstract class BusinessRuleException extends RuntimeException {
    
    private final String errorCode;
    private final Object[] parameters;
    
    protected BusinessRuleException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }
    
    protected BusinessRuleException(String message, String errorCode, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }
    
    protected BusinessRuleException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }
    
    protected BusinessRuleException(String message, Throwable cause, String errorCode, Object... parameters) {
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
