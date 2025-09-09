package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.AtualizarConsultaUseCase;
import com.medsync.cadastroagendamento.application.usecases.CriarConsultaUseCase;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.dto.ConsultaResponse;
import com.medsync.cadastroagendamento.presentation.mappers.ConsultaDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {
    
    private final CriarConsultaUseCase criarConsultaUseCase;
    private final AtualizarConsultaUseCase atualizarConsultaUseCase;
    private final ConsultaGateway consultaGateway;
    private final ConsultaDtoMapper mapper;
    
    public ConsultaController(CriarConsultaUseCase criarConsultaUseCase,
                             AtualizarConsultaUseCase atualizarConsultaUseCase,
                             ConsultaGateway consultaGateway,
                             ConsultaDtoMapper mapper) {
        this.criarConsultaUseCase = criarConsultaUseCase;
        this.atualizarConsultaUseCase = atualizarConsultaUseCase;
        this.consultaGateway = consultaGateway;
        this.mapper = mapper;
    }
    
    @PostMapping
    public ResponseEntity<ConsultaResponse> criarConsulta(@Valid @RequestBody CriarConsultaRequest request) {
        var useCaseRequest = mapper.toUseCaseRequest(request);
        var consulta = criarConsultaUseCase.executar(useCaseRequest);
        var response = mapper.toResponse(consulta);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> buscarPorId(@PathVariable UUID id) {
        var consulta = consultaGateway.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        var response = mapper.toResponse(consulta);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ConsultaResponse>> buscarTodas() {
        var consultas = consultaGateway.buscarTodas();
        var response = mapper.toResponseList(consultas);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ConsultaResponse>> buscarPorPaciente(@PathVariable UUID pacienteId) {
        var consultas = consultaGateway.buscarPorPaciente(pacienteId);
        var response = mapper.toResponseList(consultas);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<List<ConsultaResponse>> buscarPorMedico(@PathVariable UUID medicoId) {
        var consultas = consultaGateway.buscarPorMedico(medicoId);
        var response = mapper.toResponseList(consultas);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> atualizarConsulta(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarConsultaUseCase.AtualizarConsultaRequest request) {
        var consulta = atualizarConsultaUseCase.executar(id, request);
        var response = mapper.toResponse(consulta);
        return ResponseEntity.ok(response);
    }
}
