package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.AtualizarUsuarioUseCase;
import com.medsync.cadastroagendamento.application.usecases.BuscarUsuarioUseCase;
import com.medsync.cadastroagendamento.application.usecases.CriarUsuarioUseCase;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.UsuarioResponse;
import com.medsync.cadastroagendamento.presentation.mappers.UsuarioDtoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final BuscarUsuarioUseCase buscarUsuarioUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final UsuarioDtoMapper mapper;
    
    public UsuarioController(CriarUsuarioUseCase criarUsuarioUseCase,
                            BuscarUsuarioUseCase buscarUsuarioUseCase,
                            AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                            UsuarioDtoMapper mapper) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.buscarUsuarioUseCase = buscarUsuarioUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
        this.mapper = mapper;
    }
    
    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        var useCaseRequest = mapper.toUseCaseRequest(request);
        var usuario = criarUsuarioUseCase.executar(useCaseRequest);
        var response = mapper.toResponse(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable UUID id) {
        var usuario = buscarUsuarioUseCase.buscarPorId(id);
        var response = mapper.toResponse(usuario);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> buscarTodos() {
        var usuarios = buscarUsuarioUseCase.buscarTodos();
        var response = mapper.toResponseList(usuarios);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/medicos")
    public ResponseEntity<List<UsuarioResponse>> buscarMedicos() {
        var medicos = buscarUsuarioUseCase.buscarMedicos();
        var response = mapper.toResponseList(medicos);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/pacientes")
    public ResponseEntity<List<UsuarioResponse>> buscarPacientes() {
        var pacientes = buscarUsuarioUseCase.buscarPacientes();
        var response = mapper.toResponseList(pacientes);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarUsuarioUseCase.AtualizarUsuarioRequest request) {
        var usuario = atualizarUsuarioUseCase.executar(id, request);
        var response = mapper.toResponse(usuario);
        return ResponseEntity.ok(response);
    }
}
