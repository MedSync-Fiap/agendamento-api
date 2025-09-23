package com.medsync.cadastroagendamento.infrastructure.clients;

import com.medsync.cadastroagendamento.presentation.dto.ConsultaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class ConsultaFeignClientFallback implements ConsultaFeignClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsultaFeignClientFallback.class);
    
    @Override
    public List<ConsultaResponse> buscarTodasConsultas() {
        logger.warn("Fallback executado para busca de todas as consultas");
        return Collections.emptyList();
    }
    
    @Override
    public ConsultaResponse buscarConsultaPorId(UUID id) {
        logger.warn("Fallback executado para busca de consulta por ID: {}", id);
        return null;
    }
    
    @Override
    public List<ConsultaResponse> buscarConsultasPorPaciente(UUID pacienteId) {
        logger.warn("Fallback executado para busca de consultas do paciente: {}", pacienteId);
        return Collections.emptyList();
    }
    
    @Override
    public List<ConsultaResponse> buscarConsultasPorMedico(UUID medicoId) {
        logger.warn("Fallback executado para busca de consultas do médico: {}", medicoId);
        return Collections.emptyList();
    }
}
