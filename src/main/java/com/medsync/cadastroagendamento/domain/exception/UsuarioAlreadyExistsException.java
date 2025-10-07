package com.medsync.cadastroagendamento.domain.exception;

public class UsuarioAlreadyExistsException extends BusinessRuleException {
    
    public static final String ERROR_CODE = "USUARIO_ALREADY_EXISTS";
    
    public UsuarioAlreadyExistsException(String message) {
        super(message, ERROR_CODE);
    }
    
    public UsuarioAlreadyExistsException(String message, Object... parameters) {
        super(message, ERROR_CODE, parameters);
    }
    
    public static UsuarioAlreadyExistsException byEmail(String email) {
        return new UsuarioAlreadyExistsException("Usuário já existe com email: " + email, email);
    }
    
    public static UsuarioAlreadyExistsException byCpf(String cpf) {
        return new UsuarioAlreadyExistsException("Usuário já existe com CPF: " + cpf, cpf);
    }
}
