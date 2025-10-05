package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.AtualizarConsultaUseCase;
import com.medsync.cadastroagendamento.application.usecases.CriarConsultaUseCase;
import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoPatientClient;
import com.medsync.cadastroagendamento.infrastructure.security.RequirePermission;
import com.medsync.cadastroagendamento.infrastructure.security.SecurityUtils;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/consultas")
@Tag(name = "Consultas", description = "API para gerenciamento de consultas médicas")
public class ConsultaController {
    
    private final CriarConsultaUseCase criarConsultaUseCase;
    private final AtualizarConsultaUseCase atualizarConsultaUseCase;
    private final HistoricoPatientClient historicoPatientClient;
    
    public ConsultaController(CriarConsultaUseCase criarConsultaUseCase,
                            AtualizarConsultaUseCase atualizarConsultaUseCase,
                            HistoricoPatientClient historicoPatientClient) {
        this.criarConsultaUseCase = criarConsultaUseCase;
        this.atualizarConsultaUseCase = atualizarConsultaUseCase;
        this.historicoPatientClient = historicoPatientClient;
    }
    
    @PostMapping
    @RequirePermission("CRIAR_CONSULTA")
    @Operation(summary = "Criar nova consulta", description = "Cria uma nova consulta médica no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para criar consultas")
    })
    public ResponseEntity<String> criarConsulta(
            @Valid @RequestBody CriarConsultaRequest request) {
        UUID usuarioLogadoId = SecurityUtils.getCurrentUserId();
        
        // Criar consulta via GraphQL no serviço de histórico
        criarConsultaUseCase.executar(request, usuarioLogadoId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Consulta criada com sucesso e enviada para o histórico");
    }
    
    @GetMapping("/{id}/paciente/{pacienteId}")
    @RequirePermission("VISUALIZAR_CONSULTAS")
    @Operation(summary = "Buscar consulta por ID", description = "Retorna uma consulta específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta encontrada"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para visualizar consultas")
    })
    public ResponseEntity<Map<String, Object>> buscarPorId(
            @Parameter(description = "ID da consulta") @PathVariable UUID id,
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        // Buscar consulta no histórico via GraphQL
        Map<String, Object> consulta = historicoPatientClient.buscarConsulta(id, pacienteId);
        return ResponseEntity.ok(consulta);
    }
    
    @GetMapping("/paciente/{pacienteId}")
    @RequirePermission("VISUALIZAR_HISTORICO")
    @Operation(summary = "Buscar consultas por paciente", description = "Retorna todas as consultas de um paciente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de consultas do paciente retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para visualizar histórico")
    })
    public ResponseEntity<Map<String, Object>> buscarPorPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        // Buscar histórico completo do paciente no histórico via GraphQL
        Map<String, Object> historico = historicoPatientClient.buscarHistoricoCompleto(pacienteId);
        return ResponseEntity.ok(historico);
    }
    
    @PutMapping("/{id}")
    @RequirePermission("EDITAR_CONSULTA")
    @Operation(summary = "Atualizar consulta", description = "Atualiza os dados de uma consulta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para editar consultas")
    })
    public ResponseEntity<String> atualizarConsulta(
            @Parameter(description = "ID da consulta") @PathVariable UUID id,
            @Valid @RequestBody AtualizarConsultaRequest request) {
        UUID usuarioLogadoId = SecurityUtils.getCurrentUserId();
        
        // Atualizar consulta via GraphQL no serviço de histórico
        // Os IDs do paciente e médico são obtidos do request
        atualizarConsultaUseCase.executar(id, request.pacienteId(), request.medicoId(), request, usuarioLogadoId);
        
        return ResponseEntity.ok("Consulta atualizada com sucesso no histórico");
    }
}
