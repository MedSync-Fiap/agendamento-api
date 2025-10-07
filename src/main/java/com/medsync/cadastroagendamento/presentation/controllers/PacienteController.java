package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoPatientClient;
import com.medsync.cadastroagendamento.infrastructure.security.RequirePermission;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarPacienteRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarPacienteRequest;
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
@RequestMapping("/pacientes")
@Tag(name = "Pacientes", description = "API para gerenciamento de pacientes via histórico")
public class PacienteController {
    
    private final HistoricoPatientClient historicoPatientClient;
    
    public PacienteController(HistoricoPatientClient historicoPatientClient) {
        this.historicoPatientClient = historicoPatientClient;
    }
    
    @PostMapping
    @RequirePermission("CRIAR_USUARIO")
    @Operation(summary = "Criar novo paciente", description = "Cria um novo paciente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Paciente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Paciente já existe"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para criar pacientes")
    })
    public ResponseEntity<Map<String, Object>> criarPaciente(
            @Valid @RequestBody CriarPacienteRequest request) {
        
        Map<String, Object> paciente = historicoPatientClient.criarPaciente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(paciente);
    }
    
    @GetMapping("/cpf/{cpf}")
    @RequirePermission("VISUALIZAR_USUARIOS")
    @Operation(summary = "Buscar paciente por CPF", description = "Retorna dados de um paciente pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do paciente encontrados"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado no histórico"),
            @ApiResponse(responseCode = "400", description = "CPF inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para visualizar pacientes")
    })
    public ResponseEntity<Map<String, Object>> buscarPacientePorCpf(
            @Parameter(description = "CPF do paciente (apenas números)") @PathVariable String cpf) {
        
        Map<String, Object> dadosPaciente = historicoPatientClient.buscarPacientePorCpf(cpf);
        return ResponseEntity.ok(dadosPaciente);
    }
    
    @GetMapping("/{pacienteId}")
    @RequirePermission("VISUALIZAR_USUARIOS")
    @Operation(summary = "Buscar dados do paciente", description = "Retorna dados básicos de um paciente do histórico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do paciente encontrados"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado no histórico"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para visualizar pacientes")
    })
    public ResponseEntity<Map<String, Object>> buscarPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        
        Map<String, Object> dadosPaciente = historicoPatientClient.buscarPaciente(pacienteId);
        return ResponseEntity.ok(dadosPaciente);
    }
    
    @PutMapping("/{pacienteId}")
    @RequirePermission("EDITAR_USUARIO")
    @Operation(summary = "Atualizar dados do paciente", description = "Atualiza os dados de um paciente no histórico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para editar pacientes")
    })
    public ResponseEntity<Map<String, Object>> atualizarPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId,
            @Valid @RequestBody AtualizarPacienteRequest request) {
        
        Map<String, Object> paciente = historicoPatientClient.atualizarPaciente(pacienteId, request);
        return ResponseEntity.ok(paciente);
    }
    
    @DeleteMapping("/{pacienteId}")
    @RequirePermission("EXCLUIR_USUARIO")
    @Operation(summary = "Excluir paciente", description = "Remove um paciente do sistema (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para excluir pacientes")
    })
    public ResponseEntity<String> excluirPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        
        historicoPatientClient.excluirPaciente(pacienteId);
        return ResponseEntity.ok("Paciente excluído com sucesso");
    }
    
    @PutMapping("/{pacienteId}/inativar")
    @RequirePermission("EDITAR_USUARIO")
    @Operation(summary = "Inativar paciente", description = "Inativa um paciente no sistema (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente inativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para inativar pacientes")
    })
    public ResponseEntity<Map<String, Object>> inativarPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        
        Map<String, Object> pacienteInativado = historicoPatientClient.inativarPaciente(pacienteId);
        return ResponseEntity.ok(pacienteInativado);
    }
    
    @PutMapping("/{pacienteId}/reativar")
    @RequirePermission("EDITAR_USUARIO")
    @Operation(summary = "Reativar paciente", description = "Reativa um paciente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paciente reativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para reativar pacientes")
    })
    public ResponseEntity<Map<String, Object>> reativarPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        
        Map<String, Object> pacienteReativado = historicoPatientClient.reativarPaciente(pacienteId);
        return ResponseEntity.ok(pacienteReativado);
    }
    
    @GetMapping("/{pacienteId}/historico")
    @RequirePermission("VISUALIZAR_HISTORICO")
    @Operation(summary = "Buscar histórico médico completo", description = "Retorna o histórico médico completo de um paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico médico retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Paciente não encontrado no histórico"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para visualizar histórico")
    })
    public ResponseEntity<Map<String, Object>> buscarHistoricoCompleto(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        
        Map<String, Object> historico = historicoPatientClient.buscarHistoricoCompleto(pacienteId);
        return ResponseEntity.ok(historico);
    }
    
    @GetMapping("/{pacienteId}/consultas/{consultaId}")
    @RequirePermission("VISUALIZAR_CONSULTAS")
    @Operation(summary = "Buscar consulta específica", description = "Retorna dados de uma consulta específica do paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta encontrada"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para visualizar consultas")
    })
    public ResponseEntity<Map<String, Object>> buscarConsulta(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId,
            @Parameter(description = "ID da consulta") @PathVariable UUID consultaId) {
        
        Map<String, Object> consulta = historicoPatientClient.buscarConsulta(consultaId, pacienteId);
        return ResponseEntity.ok(consulta);
    }
}