package com.medsync.cadastroagendamento.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {
    
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof UUID) {
            return (UUID) authentication.getDetails();
        }
        throw new IllegalStateException("Usuário não autenticado ou ID não encontrado");
    }
    
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }
        throw new IllegalStateException("Usuário não autenticado ou email não encontrado");
    }
}
