package com.medsync.cadastroagendamento.infrastructure.clients;

import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HistoricoFeignClientFallback implements HistoricoFeignClient {
    
    private static final Logger logger = LoggerFactory.getLogger(HistoricoFeignClientFallback.class);
    
    @Override
    public HistoricoPacienteResponse buscarHistoricoPaciente(UUID pacienteId) {
        logger.warn("Fallback executado para busca de histórico do paciente: {}", pacienteId);
        
        return new HistoricoPacienteResponse(
            pacienteId,
            "Serviço de histórico indisponível",
            "",
            "",
            java.util.Collections.emptyList()
        );
    }
}
