package com.medsync.cadastroagendamento.infrastructure.clients;

import com.medsync.cadastroagendamento.presentation.dto.ConsultaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(
    name = "consulta-service",
    url = "http://localhost:8082", // URL do serviço de consultas/histórico
    fallback = ConsultaFeignClientFallback.class
)
public interface ConsultaFeignClient {
    
    @GetMapping("/consultas")
    List<ConsultaResponse> buscarTodasConsultas();
    
    @GetMapping("/consultas/{id}")
    ConsultaResponse buscarConsultaPorId(@PathVariable UUID id);
    
    @GetMapping("/consultas/paciente/{pacienteId}")
    List<ConsultaResponse> buscarConsultasPorPaciente(@PathVariable UUID pacienteId);
    
    @GetMapping("/consultas/medico/{medicoId}")
    List<ConsultaResponse> buscarConsultasPorMedico(@PathVariable UUID medicoId);
}
