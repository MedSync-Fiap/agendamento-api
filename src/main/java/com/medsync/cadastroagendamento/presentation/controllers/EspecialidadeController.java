package com.medsync.cadastroagendamento.presentation.controllers;

import com.medsync.cadastroagendamento.application.usecases.ListarEspecialidadesUseCase;
import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import com.medsync.cadastroagendamento.infrastructure.security.RequirePermission;
import com.medsync.cadastroagendamento.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/especialidades")
@Tag(name = "Especialidades", description = "Endpoints para gerenciamento de especialidades médicas")
public class EspecialidadeController {
    
    private final ListarEspecialidadesUseCase listarEspecialidadesUseCase;
    
    public EspecialidadeController(ListarEspecialidadesUseCase listarEspecialidadesUseCase) {
        this.listarEspecialidadesUseCase = listarEspecialidadesUseCase;
    }
    
    @GetMapping
    @RequirePermission("VISUALIZAR_USUARIOS")
    @Operation(summary = "Listar todas as especialidades", 
               description = "Retorna uma lista com todas as especialidades médicas cadastradas no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de especialidades retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissão necessária: VISUALIZAR_USUARIOS")
    })
    public ResponseEntity<List<Especialidade>> listarEspecialidades() {
        UUID usuarioLogadoId = SecurityUtils.getCurrentUserId();
        List<Especialidade> especialidades = listarEspecialidadesUseCase.executar();
        return ResponseEntity.ok(especialidades);
    }
}
