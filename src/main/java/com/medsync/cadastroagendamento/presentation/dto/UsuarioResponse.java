package com.medsync.cadastroagendamento.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String nome,
    String cpf,
    String email,
    UUID roleId,
    boolean ativo,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm,
    List<TelefoneResponse> telefones
) {}
