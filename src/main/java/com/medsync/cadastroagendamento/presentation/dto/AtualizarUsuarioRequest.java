package com.medsync.cadastroagendamento.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados para atualização de usuário")
public record AtualizarUsuarioRequest(
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        @Schema(description = "Nome completo do usuário", example = "João Silva")
        String nome,
        
        @Email(message = "Email deve ter um formato válido")
        @Schema(description = "Email do usuário", example = "joao@email.com")
        String email,
        
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        @Schema(description = "Nova senha do usuário", example = "novaSenha123")
        String senha,
        
        @Schema(description = "ID da role do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID roleId
) {}
