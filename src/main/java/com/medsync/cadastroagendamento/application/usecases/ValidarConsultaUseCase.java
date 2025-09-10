package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ValidarConsultaUseCase {
    
    private final ValidarPacienteUseCase validarPacienteUseCase;
    private final ValidarMedicoUseCase validarMedicoUseCase;
    private final BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private final ValidarConflitoHorarioUseCase validarConflitoHorarioUseCase;
    
    public ValidarConsultaUseCase(ValidarPacienteUseCase validarPacienteUseCase,
                                  ValidarMedicoUseCase validarMedicoUseCase,
                                  BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase,
                                  ValidarConflitoHorarioUseCase validarConflitoHorarioUseCase) {
        this.validarPacienteUseCase = validarPacienteUseCase;
        this.validarMedicoUseCase = validarMedicoUseCase;
        this.buscarUsuarioPorIdUseCase = buscarUsuarioPorIdUseCase;
        this.validarConflitoHorarioUseCase = validarConflitoHorarioUseCase;
    }
    
    public void validarCriacaoConsulta(CriarConsultaRequest request) {
        validarPacienteUseCase.executar(request.pacienteId());
        validarMedicoUseCase.executar(request.medicoId());
        buscarUsuarioPorIdUseCase.executar(request.criadoPorId());
        validarConflitoHorarioUseCase.executar(request.medicoId(), request.dataHora());
    }
    
    public void validarAtualizacaoConsulta(UUID consultaId, UUID medicoId, LocalDateTime dataHora) {
        if (medicoId != null && dataHora != null) {
            validarConflitoHorarioUseCase.executarExcluindo(medicoId, dataHora, consultaId);
        }
    }
}
