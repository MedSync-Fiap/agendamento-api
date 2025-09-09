package com.medsync.cadastroagendamento.presentation.dto;

import com.medsync.cadastroagendamento.domain.enums.TipoTelefone;

import java.util.UUID;

public record TelefoneResponse(
    UUID id,
    UUID usuarioId,
    String numero,
    TipoTelefone tipo
) {}
