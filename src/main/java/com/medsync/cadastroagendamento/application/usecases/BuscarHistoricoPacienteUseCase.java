package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.application.services.HistoricoService;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class BuscarHistoricoPacienteUseCase {
    
    private final UsuarioGateway usuarioGateway;
    private final HistoricoService historicoService;
    
    public BuscarHistoricoPacienteUseCase(UsuarioGateway usuarioGateway, HistoricoService historicoService) {
        this.usuarioGateway = usuarioGateway;
        this.historicoService = historicoService;
    }
    
    public HistoricoPacienteResponse executar(UUID pacienteId, UUID usuarioLogadoId) {
        // Validar se o paciente existe
        Usuario paciente = usuarioGateway.buscarPorId(pacienteId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(pacienteId));
        
        // Validar se o usuário logado tem permissão para acessar o histórico
        validarPermissaoAcessoHistorico(pacienteId, usuarioLogadoId);
        
        // Buscar histórico no serviço de histórico
        return historicoService.buscarHistoricoPaciente(pacienteId);
    }
    
    private void validarPermissaoAcessoHistorico(UUID pacienteId, UUID usuarioLogadoId) {
        Usuario usuarioLogado = usuarioGateway.buscarPorId(usuarioLogadoId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioLogadoId));
        
        // Pacientes só podem ver seu próprio histórico
        // Médicos, enfermeiros e admins podem ver qualquer histórico
        if (usuarioLogado.isPaciente() && !pacienteId.equals(usuarioLogadoId)) {
            throw new SecurityException("Pacientes só podem visualizar seu próprio histórico");
        }
        
        // Verificar se o usuário tem permissão para visualizar histórico
        if (!usuarioLogado.hasPermission("VISUALIZAR_HISTORICO")) {
            throw new SecurityException("Usuário não tem permissão para visualizar histórico");
        }
    }
}

