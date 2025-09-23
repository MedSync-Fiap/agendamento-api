package com.medsync.cadastroagendamento.infrastructure.clients;

import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
    name = "historico-service",
    url = "http://localhost:8081",
    fallback = HistoricoFeignClientFallback.class
)
public interface HistoricoFeignClient {
    
    @GetMapping("/historico/paciente/{pacienteId}")
    HistoricoPacienteResponse buscarHistoricoPaciente(@PathVariable UUID pacienteId);
}
