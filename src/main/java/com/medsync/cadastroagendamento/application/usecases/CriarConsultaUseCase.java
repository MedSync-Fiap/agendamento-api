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
    private final ValidarPermissaoPacienteUseCase validarPermissaoPacienteUseCase;
    private final PublicarEventoConsultaUseCase publicarEventoConsultaUseCase;
    
    public CriarConsultaUseCase(ConsultaGateway consultaGateway,
                                ValidarConsultaUseCase validarConsultaUseCase,
                                ValidarPermissaoPacienteUseCase validarPermissaoPacienteUseCase,
                                PublicarEventoConsultaUseCase publicarEventoConsultaUseCase) {
        this.consultaGateway = consultaGateway;
        this.validarConsultaUseCase = validarConsultaUseCase;
        this.validarPermissaoPacienteUseCase = validarPermissaoPacienteUseCase;
        this.publicarEventoConsultaUseCase = publicarEventoConsultaUseCase;
    }
    
    public Consulta executar(CriarConsultaRequest request, UUID usuarioLogadoId) {
        // Validar permissões do usuário logado
        validarPermissaoPacienteUseCase.validarCriacaoConsulta(request.pacienteId(), usuarioLogadoId);
        
        // Validar dados da consulta
        validarConsultaUseCase.validarCriacaoConsulta(request, usuarioLogadoId);
        
        Consulta consulta = criarConsulta(request, usuarioLogadoId);
        Consulta consultaSalva = consultaGateway.salvar(consulta);
        
        publicarEventoConsultaUseCase.publicarConsultaCriada(consultaSalva);
        
        return consultaSalva;
    }
    
    private Consulta criarConsulta(CriarConsultaRequest request, UUID usuarioLogadoId) {
        Consulta consulta = new Consulta();
        consulta.setId(UUID.randomUUID());
        consulta.setPacienteId(request.pacienteId());
        consulta.setMedicoId(request.medicoId());
        consulta.setCriadoPorId(usuarioLogadoId);
        consulta.setDataHora(request.dataHora());
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setObservacoes(request.observacoes());
        consulta.setCriadoEm(LocalDateTime.now());
        consulta.setAtualizadoEm(LocalDateTime.now());
        return consulta;
    }
}
