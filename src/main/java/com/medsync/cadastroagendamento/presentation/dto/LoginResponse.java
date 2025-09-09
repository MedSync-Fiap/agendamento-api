package com.medsync.cadastroagendamento.presentation.dto;

public record LoginResponse(
    String token,
    String tipo,
    UsuarioResponse usuario
) {}
