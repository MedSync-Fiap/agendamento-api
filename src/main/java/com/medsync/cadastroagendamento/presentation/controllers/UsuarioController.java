package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.AtualizarUsuarioUseCase;
import com.medsync.cadastroagendamento.application.usecases.BuscarUsuariosPorRoleUseCase;
import com.medsync.cadastroagendamento.application.usecases.CriarUsuarioUseCase;
import com.medsync.cadastroagendamento.application.usecases.DeletarUsuarioUseCase;
import com.medsync.cadastroagendamento.application.usecases.ListarUsuariosUseCase;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public class UsuarioController {

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final DeletarUsuarioUseCase deletarUsuarioUseCase;
    private final BuscarUsuariosPorRoleUseCase buscarUsuariosPorRoleUseCase;

    public UsuarioController(CriarUsuarioUseCase criarUsuarioUseCase,
                            ListarUsuariosUseCase listarUsuariosUseCase,
                            AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                            DeletarUsuarioUseCase deletarUsuarioUseCase,
                            BuscarUsuariosPorRoleUseCase buscarUsuariosPorRoleUseCase) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
        this.deletarUsuarioUseCase = deletarUsuarioUseCase;
        this.buscarUsuariosPorRoleUseCase = buscarUsuariosPorRoleUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CRIAR_USUARIO')")
    @Operation(summary = "Criar usuário", description = "Cria um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já existe"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: CRIAR_USUARIO")
    })
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        Usuario usuario = criarUsuarioUseCase.executar(request);
        UsuarioResponse response = UsuarioResponse.fromDomain(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VISUALIZAR_USUARIOS')")
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_USUARIOS")
    })
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<Usuario> usuarios = listarUsuariosUseCase.executar();
        List<UsuarioResponse> responses = usuarios.stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VISUALIZAR_USUARIOS')")
    @Operation(summary = "Buscar usuário por ID", description = "Busca um usuário específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_USUARIOS")
    })
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        Usuario usuario = listarUsuariosUseCase.buscarPorId(id);
        UsuarioResponse response = UsuarioResponse.fromDomain(usuario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/medicos")
    @PreAuthorize("hasAuthority('VISUALIZAR_USUARIOS')")
    @Operation(summary = "Listar médicos", description = "Lista todos os médicos do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de médicos retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_USUARIOS")
    })
    public ResponseEntity<List<UsuarioResponse>> listarMedicos() {
        List<Usuario> usuarios = buscarUsuariosPorRoleUseCase.executar("MEDICO");
        List<UsuarioResponse> responses = usuarios.stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pacientes")
    @PreAuthorize("hasAuthority('VISUALIZAR_USUARIOS')")
    @Operation(summary = "Listar pacientes", description = "Lista todos os pacientes do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pacientes retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_USUARIOS")
    })
    public ResponseEntity<List<UsuarioResponse>> listarPacientes() {
        List<Usuario> usuarios = buscarUsuariosPorRoleUseCase.executar("PACIENTE");
        List<UsuarioResponse> responses = usuarios.stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/enfermeiros")
    @PreAuthorize("hasAuthority('VISUALIZAR_USUARIOS')")
    @Operation(summary = "Listar enfermeiros", description = "Lista todos os enfermeiros do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de enfermeiros retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_USUARIOS")
    })
    public ResponseEntity<List<UsuarioResponse>> listarEnfermeiros() {
        List<Usuario> usuarios = buscarUsuariosPorRoleUseCase.executar("ENFERMEIRO");
        List<UsuarioResponse> responses = usuarios.stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasAuthority('VISUALIZAR_USUARIOS')")
    @Operation(summary = "Buscar usuários por role", description = "Busca usuários por tipo de role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_USUARIOS")
    })
    public ResponseEntity<List<UsuarioResponse>> buscarUsuariosPorRole(
            @Parameter(description = "Tipo de role (MEDICO, PACIENTE, ENFERMEIRO, ADMIN)") @PathVariable String role) {
        List<Usuario> usuarios = buscarUsuariosPorRoleUseCase.executar(role);
        List<UsuarioResponse> responses = usuarios.stream()
                .map(UsuarioResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITAR_USUARIO')")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Email já existe"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: EDITAR_USUARIO")
    })
    public ResponseEntity<UsuarioResponse> atualizarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @Valid @RequestBody AtualizarUsuarioRequest request) {
        Usuario usuario = atualizarUsuarioUseCase.executar(id, request);
        UsuarioResponse response = UsuarioResponse.fromDomain(usuario);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EXCLUIR_USUARIO')")
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: EXCLUIR_USUARIO")
    })
    public ResponseEntity<Void> deletarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        deletarUsuarioUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
