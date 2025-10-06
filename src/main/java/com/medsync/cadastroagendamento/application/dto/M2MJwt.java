package com.medsync.cadastroagendamento.application.dto;

import java.time.Instant;

public record M2MJwt(
    String token,
    Instant expiresAt
) {
}
