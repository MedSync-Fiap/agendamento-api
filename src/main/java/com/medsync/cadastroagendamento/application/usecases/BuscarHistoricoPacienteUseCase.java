package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.infrastructure.clients.HistoricoFeignClient;
import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BuscarHistoricoPacienteUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(BuscarHistoricoPacienteUseCase.class);
    
    private final HistoricoFeignClient historicoFeignClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    
    public BuscarHistoricoPacienteUseCase(HistoricoFeignClient historicoFeignClient, 
                                        CircuitBreakerFactory circuitBreakerFactory) {
        this.historicoFeignClient = historicoFeignClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }
    
    public HistoricoPacienteResponse executar(UUID pacienteId, UUID usuarioLogadoId) {
        logger.info("Buscando histórico do paciente: {} pelo usuário: {}", pacienteId, usuarioLogadoId);
        
        return circuitBreakerFactory.create("historico-service")
            .run(() -> {
                try {
                    HistoricoPacienteResponse response = historicoFeignClient.buscarHistoricoPaciente(pacienteId);
                    
                    logger.info("Histórico encontrado para paciente: {} com {} consultas", 
                               pacienteId, response.consultas().size());
                    
                    return response;
                    
                } catch (Exception e) {
                    logger.error("Erro ao buscar histórico do paciente: {}", pacienteId, e);
                    throw e;
                }
            }, throwable -> {
                logger.warn("Fallback executado para busca de histórico do paciente: {} - Erro: {}", 
                           pacienteId, throwable.getMessage());
                
                return new HistoricoPacienteResponse(
                    pacienteId,
                    "Histórico temporariamente indisponível",
                    "",
                    "",
                    java.util.Collections.emptyList()
                );
            });
    }
}
