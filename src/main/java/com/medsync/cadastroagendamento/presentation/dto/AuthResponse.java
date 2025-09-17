package com.medsync.cadastroagendamento.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AuthResponse(
    String accessToken,
    String tokenType,
    Long expiresIn,
    LocalDateTime expiresAt,
    UserInfo user
) {
    
    public record UserInfo(
        String id,
        String nome,
        String email,
        String role,
        List<String> permissions
    ) {}
}

