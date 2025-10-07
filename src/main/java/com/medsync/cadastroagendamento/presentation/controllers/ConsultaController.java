package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.AtualizarConsultaUseCase;
import com.medsync.cadastroagendamento.application.usecases.CriarConsultaUseCase;
import com.medsync.cadastroagendamento.application.usecases.DeletarConsultaUseCase;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/consultas")
@Tag(name = "Consultas", description = "API para gerenciamento de consultas médicas")
public class ConsultaController {
    
    private final CriarConsultaUseCase criarConsultaUseCase;
    private final AtualizarConsultaUseCase atualizarConsultaUseCase;
    private final DeletarConsultaUseCase deletarConsultaUseCase;
    private final HistoricoPatientClient historicoPatientClient;
    
    public ConsultaController(CriarConsultaUseCase criarConsultaUseCase,
                            AtualizarConsultaUseCase atualizarConsultaUseCase,
                            DeletarConsultaUseCase deletarConsultaUseCase,
                            HistoricoPatientClient historicoPatientClient) {
        this.criarConsultaUseCase = criarConsultaUseCase;
        this.atualizarConsultaUseCase = atualizarConsultaUseCase;
        this.deletarConsultaUseCase = deletarConsultaUseCase;
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
    public ResponseEntity<Map<String, Object>> criarConsulta(
            @Valid @RequestBody CriarConsultaRequest request) {
        UUID usuarioLogadoId = SecurityUtils.getCurrentUserId();
        
        UUID consultaId = criarConsultaUseCase.executar(request, usuarioLogadoId);
        
        // Retornar apenas o ID da consulta criada para evitar busca dupla
        Map<String, Object> response = new HashMap<>();
        response.put("id", consultaId);
        response.put("message", "Consulta criada com sucesso");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    public ResponseEntity<List<Map<String, Object>>> buscarPorPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        Map<String, Object> historico = historicoPatientClient.buscarHistoricoCompleto(pacienteId);
        
        if (historico != null && historico.containsKey("appointments")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> appointments = (List<Map<String, Object>>) historico.get("appointments");
            return ResponseEntity.ok(appointments);
        }
        
        return ResponseEntity.ok(new ArrayList<>());
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
        
        atualizarConsultaUseCase.executar(id, request, usuarioLogadoId);
        
        return ResponseEntity.ok("Consulta atualizada com sucesso no histórico");
    }
    
    @DeleteMapping("/{id}")
    @RequirePermission("EXCLUIR_CONSULTA")
    @Operation(summary = "Deletar consulta", description = "Remove uma consulta do sistema (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Consulta deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para excluir consultas")
    })
    public ResponseEntity<Void> deletarConsulta(
            @Parameter(description = "ID da consulta") @PathVariable UUID id,
            @Parameter(description = "ID do paciente") @RequestParam UUID pacienteId) {
        UUID usuarioLogadoId = SecurityUtils.getCurrentUserId();
        
        // Soft delete - marca como inativa ao invés de deletar fisicamente
        deletarConsultaUseCase.executar(id, pacienteId, usuarioLogadoId);
        
        return ResponseEntity.noContent().build();
    }
}
