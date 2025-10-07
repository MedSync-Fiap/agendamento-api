package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.AtualizarUsuarioUseCase;
import com.medsync.cadastroagendamento.application.usecases.BuscarUsuariosPorRoleUseCase;
import com.medsync.cadastroagendamento.application.usecases.CriarUsuarioUseCase;
import com.medsync.cadastroagendamento.application.usecases.DeletarUsuarioUseCase;
import com.medsync.cadastroagendamento.application.usecases.ListarUsuariosUseCase;
import com.medsync.cadastroagendamento.application.usecases.ListarUsuariosInativosUseCase;
import com.medsync.cadastroagendamento.application.usecases.ReativarUsuarioUseCase;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    private final DeletarUsuarioUseCase deletarUsuarioUseCase;
    private final BuscarUsuariosPorRoleUseCase buscarUsuariosPorRoleUseCase;
    private final ListarUsuariosInativosUseCase listarUsuariosInativosUseCase;
    private final ReativarUsuarioUseCase reativarUsuarioUseCase;

    public UsuarioController(CriarUsuarioUseCase criarUsuarioUseCase,
                            ListarUsuariosUseCase listarUsuariosUseCase,
                            AtualizarUsuarioUseCase atualizarUsuarioUseCase,
                            DeletarUsuarioUseCase deletarUsuarioUseCase,
                            BuscarUsuariosPorRoleUseCase buscarUsuariosPorRoleUseCase,
                            ListarUsuariosInativosUseCase listarUsuariosInativosUseCase,
                            ReativarUsuarioUseCase reativarUsuarioUseCase) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
        this.deletarUsuarioUseCase = deletarUsuarioUseCase;
        this.buscarUsuariosPorRoleUseCase = buscarUsuariosPorRoleUseCase;
        this.listarUsuariosInativosUseCase = listarUsuariosInativosUseCase;
        this.reativarUsuarioUseCase = reativarUsuarioUseCase;
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
        try {
            Usuario usuario = criarUsuarioUseCase.executar(request);
            UsuarioResponse response = UsuarioResponse.fromDomain(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage(), e);
            throw e; // Deixa o GlobalExceptionHandler tratar
        }
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
        try {
            List<Usuario> usuarios = listarUsuariosUseCase.executar();
            List<UsuarioResponse> responses = usuarios.stream()
                    .map(UsuarioResponse::fromDomain)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Erro ao listar usuários: {}", e.getMessage(), e);
            throw e; // Deixa o GlobalExceptionHandler tratar
        }
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
        try {
            Usuario usuario = listarUsuariosUseCase.buscarPorId(id);
            UsuarioResponse response = UsuarioResponse.fromDomain(usuario);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por ID {}: {}", id, e.getMessage(), e);
            throw e; // Deixa o GlobalExceptionHandler tratar
        }
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
    @Operation(summary = "Deletar usuário", description = "Remove um usuário do sistema (soft delete)")
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

    @GetMapping("/inativos")
    @PreAuthorize("hasAuthority('VISUALIZAR_USUARIOS')")
    @Operation(summary = "Listar usuários inativos", description = "Lista todos os usuários inativos do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários inativos retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_USUARIOS")
    })
    public ResponseEntity<List<UsuarioResponse>> listarUsuariosInativos() {
        try {
            List<Usuario> usuarios = listarUsuariosInativosUseCase.executar();
            List<UsuarioResponse> responses = usuarios.stream()
                    .map(UsuarioResponse::fromDomain)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Erro ao listar usuários inativos: {}", e.getMessage(), e);
            throw e; // Deixa o GlobalExceptionHandler tratar
        }
    }

    @PutMapping("/{id}/reativar")
    @PreAuthorize("hasAuthority('EDITAR_USUARIO')")
    @Operation(summary = "Reativar usuário", description = "Reativa um usuário que foi deletado (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário reativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: EDITAR_USUARIO")
    })
    public ResponseEntity<UsuarioResponse> reativarUsuario(
            @Parameter(description = "ID do usuário") @PathVariable UUID id) {
        try {
            reativarUsuarioUseCase.executar(id);
            Usuario usuario = listarUsuariosUseCase.buscarPorId(id);
            UsuarioResponse response = UsuarioResponse.fromDomain(usuario);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao reativar usuário {}: {}", id, e.getMessage(), e);
            throw e; // Deixa o GlobalExceptionHandler tratar
        }
    }
}
