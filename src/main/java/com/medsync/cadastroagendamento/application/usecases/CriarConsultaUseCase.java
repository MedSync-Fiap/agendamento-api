package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CriarConsultaUseCase {
    
    private final ConsultaGateway consultaGateway;
    private final ValidarConsultaUseCase validarConsultaUseCase;
    private final PublicarEventoConsultaUseCase publicarEventoConsultaUseCase;
    
    public CriarConsultaUseCase(ConsultaGateway consultaGateway,
                                ValidarConsultaUseCase validarConsultaUseCase,
                                PublicarEventoConsultaUseCase publicarEventoConsultaUseCase) {
        this.consultaGateway = consultaGateway;
        this.validarConsultaUseCase = validarConsultaUseCase;
        this.publicarEventoConsultaUseCase = publicarEventoConsultaUseCase;
    }
    
    public Consulta executar(CriarConsultaRequest request) {
        validarConsultaUseCase.validarCriacaoConsulta(request);
        
        Consulta consulta = criarConsulta(request);
        Consulta consultaSalva = consultaGateway.salvar(consulta);
        
        publicarEventoConsultaUseCase.publicarConsultaCriada(consultaSalva);
        
        return consultaSalva;
    }
    
    private Consulta criarConsulta(CriarConsultaRequest request) {
        Consulta consulta = new Consulta();
        consulta.setId(UUID.randomUUID());
        consulta.setPacienteId(request.pacienteId());
        consulta.setMedicoId(request.medicoId());
        consulta.setCriadoPorId(request.criadoPorId());
        consulta.setDataHora(request.dataHora());
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setObservacoes(request.observacoes());
        consulta.setCriadoEm(LocalDateTime.now());
        consulta.setAtualizadoEm(LocalDateTime.now());
        return consulta;
    }
}
