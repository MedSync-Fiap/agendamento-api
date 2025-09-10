package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.application.services.UsuarioService;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.UsuarioResponse;
import com.medsync.cadastroagendamento.presentation.mappers.UsuarioDtoMapper;
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
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários do sistema")
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    private final UsuarioDtoMapper mapper;
    
    public UsuarioController(UsuarioService usuarioService, UsuarioDtoMapper mapper) {
        this.usuarioService = usuarioService;
        this.mapper = mapper;
    }
    
    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já existem")
    })
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        var useCaseRequest = mapper.toUseCaseRequest(request);
        var usuario = usuarioService.criarUsuario(useCaseRequest);
        var response = mapper.toResponse(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        var usuario = usuarioService.buscarPorId(id);
        var response = mapper.toResponse(usuario);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna todos os usuários cadastrados no sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso")
    public ResponseEntity<List<UsuarioResponse>> buscarTodos() {
        var usuarios = usuarioService.buscarTodos();
        var response = mapper.toResponseList(usuarios);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/medicos")
    @Operation(summary = "Listar médicos", description = "Retorna todos os usuários com role de médico")
    @ApiResponse(responseCode = "200", description = "Lista de médicos retornada com sucesso")
    public ResponseEntity<List<UsuarioResponse>> buscarMedicos() {
        var medicos = usuarioService.buscarMedicos();
        var response = mapper.toResponseList(medicos);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/pacientes")
    @Operation(summary = "Listar pacientes", description = "Retorna todos os usuários com role de paciente")
    @ApiResponse(responseCode = "200", description = "Lista de pacientes retornada com sucesso")
    public ResponseEntity<List<UsuarioResponse>> buscarPacientes() {
        var pacientes = usuarioService.buscarPacientes();
        var response = mapper.toResponseList(pacientes);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Email já existe")
    })
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @Valid @RequestBody AtualizarUsuarioRequest request) {
        var useCaseRequest = mapper.toUseCaseRequest(request);
        var usuario = usuarioService.atualizarUsuario(id, useCaseRequest);
        var response = mapper.toResponse(usuario);
        return ResponseEntity.ok(response);
    }
}
