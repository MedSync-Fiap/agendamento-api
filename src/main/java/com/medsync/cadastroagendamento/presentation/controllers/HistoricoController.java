package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.BuscarHistoricoPacienteUseCase;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/historico")
@Tag(name = "Histórico", description = "API para consulta de histórico de pacientes")
@SecurityRequirement(name = "bearerAuth")
public class HistoricoController {
    
    private final BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase;
    
    public HistoricoController(BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase) {
        this.buscarHistoricoPacienteUseCase = buscarHistoricoPacienteUseCase;
    }
    
    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Buscar histórico de paciente", description = "Retorna o histórico completo de consultas de um paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este histórico")
    })
    public ResponseEntity<HistoricoPacienteResponse> buscarHistoricoPaciente(
            @PathVariable UUID pacienteId,
            @RequestHeader("X-User-Id") UUID usuarioLogadoId) {
        
        var response = buscarHistoricoPacienteUseCase.executar(pacienteId, usuarioLogadoId);
        return ResponseEntity.ok(response);
    }
}

