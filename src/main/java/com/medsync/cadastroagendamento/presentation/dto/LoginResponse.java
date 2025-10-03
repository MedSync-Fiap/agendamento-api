package com.medsync.cadastroagendamento.presentation.dto;

import java.util.UUID;

public record LoginResponse(
    String token,
    UUID usuarioId,
    String nome,
    String email,
    String role
) {}