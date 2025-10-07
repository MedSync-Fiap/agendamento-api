package com.medsync.cadastroagendamento.domain.exception;

public class InsufficientPermissionsException extends BusinessRuleException {
    
    public static final String ERROR_CODE = "INSUFFICIENT_PERMISSIONS";
    
    public InsufficientPermissionsException(String message) {
        super(message, ERROR_CODE);
    }
    
    public InsufficientPermissionsException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public static InsufficientPermissionsException forAction(String action) {
        return new InsufficientPermissionsException("Permissões insuficientes para a ação: " + action, action);
    }
    
    public static InsufficientPermissionsException forResource(String resource) {
        return new InsufficientPermissionsException("Permissões insuficientes para o recurso: " + resource, resource);
    }
}
