package com.medsync.cadastroagendamento.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AtualizarPacienteRequest(
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        String nome,
        
        @Email(message = "Email deve ter formato válido")
        String email,
        
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
        String cpf,
        
        LocalDate dataNascimento,
        
        @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
        String observacoes
) {}
