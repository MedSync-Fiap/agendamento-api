package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.exceptions.ConsultaNaoEncontradaException;
import com.medsync.cadastroagendamento.application.services.ConsultaService;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.dto.ConsultaResponse;
import com.medsync.cadastroagendamento.presentation.mappers.ConsultaDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/consultas")
@Tag(name = "Consultas", description = "API para gerenciamento de consultas médicas")
public class ConsultaController {
    
    private final ConsultaService consultaService;
    private final ConsultaDtoMapper mapper;
    
    public ConsultaController(ConsultaService consultaService, ConsultaDtoMapper mapper) {
        this.consultaService = consultaService;
        this.mapper = mapper;
    }
    
    @PostMapping
    @Operation(summary = "Criar nova consulta", description = "Cria uma nova consulta médica no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consulta criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para criar consultas")
    })
    public ResponseEntity<ConsultaResponse> criarConsulta(
            @Valid @RequestBody CriarConsultaRequest request,
            @RequestHeader("X-User-Id") UUID usuarioLogadoId) {
        var useCaseRequest = mapper.toUseCaseRequest(request);
        var consulta = consultaService.criarConsulta(useCaseRequest, usuarioLogadoId);
        var response = mapper.toResponse(consulta);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por ID", description = "Retorna uma consulta específica pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta encontrada"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    public ResponseEntity<ConsultaResponse> buscarPorId(
            @Parameter(description = "ID da consulta") @PathVariable UUID id) {
        var consulta = consultaService.buscarPorId(id)
                .orElseThrow(() -> new ConsultaNaoEncontradaException(id));
        var response = mapper.toResponse(consulta);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as consultas", description = "Retorna todas as consultas cadastradas no sistema")
    @ApiResponse(responseCode = "200", description = "Lista de consultas retornada com sucesso")
    public ResponseEntity<List<ConsultaResponse>> buscarTodas() {
        var consultas = consultaService.buscarTodas();
        var response = mapper.toResponseList(consultas);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/paciente/{pacienteId}")
    @Operation(summary = "Buscar consultas por paciente", description = "Retorna todas as consultas de um paciente específico")
    @ApiResponse(responseCode = "200", description = "Lista de consultas do paciente retornada com sucesso")
    public ResponseEntity<List<ConsultaResponse>> buscarPorPaciente(
            @Parameter(description = "ID do paciente") @PathVariable UUID pacienteId) {
        var consultas = consultaService.buscarPorPaciente(pacienteId);
        var response = mapper.toResponseList(consultas);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/medico/{medicoId}")
    @Operation(summary = "Buscar consultas por médico", description = "Retorna todas as consultas de um médico específico")
    @ApiResponse(responseCode = "200", description = "Lista de consultas do médico retornada com sucesso")
    public ResponseEntity<List<ConsultaResponse>> buscarPorMedico(
            @Parameter(description = "ID do médico") @PathVariable UUID medicoId) {
        var consultas = consultaService.buscarPorMedico(medicoId);
        var response = mapper.toResponseList(consultas);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar consulta", description = "Atualiza os dados de uma consulta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Consulta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito de horário")
    })
    public ResponseEntity<ConsultaResponse> atualizarConsulta(
            @Parameter(description = "ID da consulta") @PathVariable UUID id,
            @Valid @RequestBody AtualizarConsultaRequest request) {
        var useCaseRequest = mapper.toUseCaseRequest(request);
        var consulta = consultaService.atualizarConsulta(id, useCaseRequest);
        var response = mapper.toResponse(consulta);
        return ResponseEntity.ok(response);
    }
}
