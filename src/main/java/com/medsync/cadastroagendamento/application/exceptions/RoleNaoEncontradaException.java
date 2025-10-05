package com.medsync.cadastroagendamento.application.exceptions;

import java.util.UUID;

public class RoleNaoEncontradaException extends RuntimeException {
    public RoleNaoEncontradaException(UUID roleId) {
        super("Role não encontrada com ID: " + roleId);
    }
}
