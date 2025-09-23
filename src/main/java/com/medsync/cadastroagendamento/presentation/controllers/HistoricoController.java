package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.services.HistoricoService;
import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoFeignClient;
import com.medsync.cadastroagendamento.infrastructure.security.RequirePermission;
import com.medsync.cadastroagendamento.infrastructure.security.SecurityUtils;
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
    
    private final HistoricoService historicoService;
    private final HistoricoFeignClient historicoFeignClient;
    
    public HistoricoController(HistoricoService historicoService, HistoricoFeignClient historicoFeignClient) {
        this.historicoService = historicoService;
        this.historicoFeignClient = historicoFeignClient;
    }
    
    @GetMapping("/paciente/{pacienteId}")
    @RequirePermission("VISUALIZAR_HISTORICO")
    @Operation(summary = "Buscar histórico de paciente", description = "Retorna o histórico completo de consultas de um paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este histórico")
    })
    public ResponseEntity<HistoricoPacienteResponse> buscarHistoricoPaciente(
            @PathVariable UUID pacienteId) {
        
        UUID usuarioLogadoId = SecurityUtils.getCurrentUserId();
        
        // Usar o Feign client para buscar o histórico diretamente do serviço de histórico
        var response = historicoFeignClient.buscarHistoricoPaciente(pacienteId);
        return ResponseEntity.ok(response);
    }
}

