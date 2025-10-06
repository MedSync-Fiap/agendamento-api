package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.dto.M2MJwt;
import com.medsync.cadastroagendamento.application.service.M2MJwtGeneratorService;
import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoPatientClient;
import com.medsync.cadastroagendamento.infrastructure.security.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/historico")
@Tag(name = "Histórico", description = "API para consulta de histórico médico")
public class HistoricoController {
    
    private final HistoricoPatientClient historicoPatientClient;
    private final M2MJwtGeneratorService jwtGeneratorService;
    
    public HistoricoController(HistoricoPatientClient historicoPatientClient, M2MJwtGeneratorService jwtGeneratorService) {
        this.historicoPatientClient = historicoPatientClient;
        this.jwtGeneratorService = jwtGeneratorService;
    }

    @GetMapping("/token")
    @RequirePermission("VISUALIZAR_HISTORICO")
    @Operation(summary = "Obter token de histórico do paciente",
            description = "Retorna o token de acesso para o histórico médico completo de um paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token obtido com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_HISTORICO")
    })
    public ResponseEntity<M2MJwt> obterTokenHistorico() {
        M2MJwt jwt = jwtGeneratorService.getTokenHistorico();
        return ResponseEntity.ok(jwt);
    }

    @GetMapping("/paciente/{pacienteId}")
    @RequirePermission("VISUALIZAR_HISTORICO")
    @Operation(summary = "Buscar histórico do paciente", 
               description = "Retorna o histórico médico completo de um paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_HISTORICO")
    })
    public ResponseEntity<Map<String, Object>> buscarHistoricoPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        
        Map<String, Object> historico = historicoPatientClient.buscarHistoricoCompleto(pacienteId);
        
        return ResponseEntity.ok(historico);
    }
    
    @GetMapping("/consulta/{consultaId}/paciente/{pacienteId}")
    @RequirePermission("VISUALIZAR_HISTORICO")
    @Operation(summary = "Buscar histórico de consulta específica", 
               description = "Retorna o histórico de uma consulta específica de um paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico da consulta encontrado"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_HISTORICO")
    })
    public ResponseEntity<Map<String, Object>> buscarHistoricoConsulta(
            @Parameter(description = "ID da consulta") @PathVariable UUID consultaId,
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        
        Map<String, Object> historico = historicoPatientClient.buscarConsulta(consultaId, pacienteId);
        
        return ResponseEntity.ok(historico);
    }
}