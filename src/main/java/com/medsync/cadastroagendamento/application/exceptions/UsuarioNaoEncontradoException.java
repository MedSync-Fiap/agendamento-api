package com.medsync.cadastroagendamento.application.exceptions;

import java.util.UUID;

public class UsuarioNaoEncontradoException extends RuntimeException {
    
    public UsuarioNaoEncontradoException(UUID id) {
        super("Usuário não encontrado com ID: " + id);
    }
    
    public UsuarioNaoEncontradoException(String email) {
        super("Usuário não encontrado com email: " + email);
    }
    
    public UsuarioNaoEncontradoException(String campo, String valor) {
        super("Usuário não encontrado com " + campo + ": " + valor);
    }
}
