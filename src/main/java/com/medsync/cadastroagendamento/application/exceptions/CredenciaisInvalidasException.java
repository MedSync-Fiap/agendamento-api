package com.medsync.cadastroagendamento.application.exceptions;

public class CredenciaisInvalidasException extends RuntimeException {
    
    public CredenciaisInvalidasException() {
        super("Email ou senha inválidos");
    }
    
    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}
