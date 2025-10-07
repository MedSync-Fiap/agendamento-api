package com.medsync.cadastroagendamento.domain.exception;

import java.util.UUID;

public class UsuarioNotFoundException extends BusinessRuleException {
    
    public static final String ERROR_CODE = "USUARIO_NOT_FOUND";
    
    public UsuarioNotFoundException(String message) {
        super(message, ERROR_CODE);
    }
    
    public UsuarioNotFoundException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public static UsuarioNotFoundException byId(UUID id) {
        return new UsuarioNotFoundException("Usuário não encontrado com ID: " + id, id);
    }
    
    public static UsuarioNotFoundException byEmail(String email) {
        return new UsuarioNotFoundException("Usuário não encontrado com email: " + email, email);
    }
}
