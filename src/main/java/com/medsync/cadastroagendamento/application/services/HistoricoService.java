package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.presentation.dto.HistoricoPacienteResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HistoricoService {
    
    // TODO: Implementar integração com o serviço de histórico via GraphQL
    // Por enquanto, retorna dados mockados
    
    public HistoricoPacienteResponse buscarHistoricoPaciente(UUID pacienteId) {
        // Esta implementação será substituída por uma chamada ao serviço de histórico
        // via GraphQL quando o serviço estiver disponível
        
        return new HistoricoPacienteResponse(
            pacienteId,
            "Paciente Mock",
            "12345678901",
            "paciente@email.com",
            List.of() // Lista vazia de consultas por enquanto
        );
    }
}

