package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.application.usecases.BuscarHistoricoPacienteUseCase;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class HistoricoService {
    
    private static final Logger logger = LoggerFactory.getLogger(HistoricoService.class);
    
    private final BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase;
    
    public HistoricoService(BuscarHistoricoPacienteUseCase buscarHistoricoPacienteUseCase) {
        this.buscarHistoricoPacienteUseCase = buscarHistoricoPacienteUseCase;
    }
    
    public HistoricoPacienteResponse buscarHistoricoPaciente(UUID pacienteId, UUID usuarioLogadoId) {
        logger.info("Service: Buscando histórico do paciente: {} pelo usuário: {}", pacienteId, usuarioLogadoId);
        
        // TODO: Implementar validações de negócio se necessário
        // - Verificar se o usuário tem permissão para acessar o histórico
        // - Validar se o paciente existe
        // - Aplicar regras de negócio específicas
        
        return buscarHistoricoPacienteUseCase.executar(pacienteId, usuarioLogadoId);
    }
}