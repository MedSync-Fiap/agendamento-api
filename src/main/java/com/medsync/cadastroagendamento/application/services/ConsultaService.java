package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.application.usecases.AtualizarConsultaUseCase;
import com.medsync.cadastroagendamento.application.usecases.CriarConsultaUseCase;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConsultaService {
    
    private final CriarConsultaUseCase criarConsultaUseCase;
    private final AtualizarConsultaUseCase atualizarConsultaUseCase;
    private final ConsultaGateway consultaGateway;
    
    public ConsultaService(CriarConsultaUseCase criarConsultaUseCase,
                          AtualizarConsultaUseCase atualizarConsultaUseCase,
                          ConsultaGateway consultaGateway) {
        this.criarConsultaUseCase = criarConsultaUseCase;
        this.atualizarConsultaUseCase = atualizarConsultaUseCase;
        this.consultaGateway = consultaGateway;
    }
    
    public Consulta criarConsulta(CriarConsultaRequest request, UUID usuarioLogadoId) {
        return criarConsultaUseCase.executar(request, usuarioLogadoId);
    }
    
    public Consulta atualizarConsulta(UUID id, AtualizarConsultaRequest request) {
        return atualizarConsultaUseCase.executar(id, request);
    }
    
    public Optional<Consulta> buscarPorId(UUID id) {
        return consultaGateway.buscarPorId(id);
    }
    
    public List<Consulta> buscarTodas() {
        return consultaGateway.buscarTodas();
    }
    
    public List<Consulta> buscarPorPaciente(UUID pacienteId) {
        return consultaGateway.buscarPorPaciente(pacienteId);
    }
    
    public List<Consulta> buscarPorMedico(UUID medicoId) {
        return consultaGateway.buscarPorMedico(medicoId);
    }
}
