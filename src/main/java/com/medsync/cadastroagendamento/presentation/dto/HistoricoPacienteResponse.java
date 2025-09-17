package com.medsync.cadastroagendamento.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record HistoricoPacienteResponse(
    UUID pacienteId,
    String pacienteNome,
    String pacienteCpf,
    String pacienteEmail,
    List<ConsultaHistorico> consultas
) {
    
    public record ConsultaHistorico(
        UUID id,
        MedicoInfo medico,
        UsuarioInfo criadoPor,
        LocalDateTime dataHora,
        String status,
        String observacoes,
        List<AcaoHistorico> acoes
    ) {}
    
    public record MedicoInfo(
        UUID id,
        String nome,
        String especialidade
    ) {}
    
    public record UsuarioInfo(
        UUID id,
        String nome
    ) {}
    
    public record AcaoHistorico(
        String tipo,
        UsuarioInfo usuario,
        Object dados,
        LocalDateTime timestamp
    ) {}
}

