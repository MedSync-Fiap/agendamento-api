package com.medsync.cadastroagendamento.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CriarConsultaRequest(
        @NotNull(message = "ID do paciente é obrigatório")
        UUID pacienteId,

        @NotNull(message = "ID do médico é obrigatório")
        UUID medicoId,

        @NotNull(message = "ID da especialidade é obrigatório")
        UUID especialidadeId,

        @NotNull(message = "Data e hora são obrigatórias")
        @Future(message = "Data e hora devem ser no futuro")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", shape = JsonFormat.Shape.STRING)
        LocalDateTime dataHora,

        String observacoes
) {}
