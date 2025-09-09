package com.medsync.cadastroagendamento.presentation.mappers;

import com.medsync.cadastroagendamento.application.usecases.CriarConsultaUseCase;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.dto.ConsultaResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsultaDtoMapper {
    
    CriarConsultaUseCase.CriarConsultaRequest toUseCaseRequest(CriarConsultaRequest dto);
    
    ConsultaResponse toResponse(Consulta consulta);
    
    List<ConsultaResponse> toResponseList(List<Consulta> consultas);
}
