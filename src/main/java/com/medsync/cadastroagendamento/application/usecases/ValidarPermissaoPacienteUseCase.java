package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ValidarPermissaoPacienteUseCase {
    
    private final UsuarioGateway usuarioGateway;
    
    public ValidarPermissaoPacienteUseCase(UsuarioGateway usuarioGateway) {
        this.usuarioGateway = usuarioGateway;
    }
    
    public void validarAcessoPaciente(UUID pacienteId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = usuarioGateway.buscarPorId(usuarioLogadoId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioLogadoId));
        
        // Se o usuário é um paciente, só pode acessar seus próprios dados
        if (usuarioLogado.isPaciente() && !pacienteId.equals(usuarioLogadoId)) {
            throw new SecurityException("Pacientes só podem acessar seus próprios dados");
        }
        
        // Verificar se o usuário tem permissão para acessar dados de pacientes
        if (!usuarioLogado.hasPermission("VISUALIZAR_CONSULTAS") && 
            !usuarioLogado.hasPermission("GERENCIAR_USUARIOS")) {
            throw new SecurityException("Usuário não tem permissão para acessar dados de pacientes");
        }
    }
    
    public void validarCriacaoConsulta(UUID pacienteId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = usuarioGateway.buscarPorId(usuarioLogadoId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioLogadoId));
        
        // Verificar se o usuário tem permissão para criar consultas
        if (!usuarioLogado.hasPermission("CRIAR_CONSULTA")) {
            throw new SecurityException("Usuário não tem permissão para criar consultas");
        }
        
        // Pacientes não podem criar consultas para si mesmos
        if (usuarioLogado.isPaciente()) {
            throw new SecurityException("Pacientes não podem criar consultas");
        }
    }
    
    public void validarEdicaoConsulta(UUID consultaId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = usuarioGateway.buscarPorId(usuarioLogadoId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioLogadoId));
        
        // Verificar se o usuário tem permissão para editar consultas
        if (!usuarioLogado.hasPermission("EDITAR_CONSULTA")) {
            throw new SecurityException("Usuário não tem permissão para editar consultas");
        }
        
        // Pacientes não podem editar consultas
        if (usuarioLogado.isPaciente()) {
            throw new SecurityException("Pacientes não podem editar consultas");
        }
    }
}

