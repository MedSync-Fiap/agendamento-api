package com.medsync.cadastroagendamento.presentation.mappers;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.dto.ConsultaResponse;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsultaDtoMapper {
    
    CriarConsultaRequest toUseCaseRequest(CriarConsultaRequest dto);
    
    AtualizarConsultaRequest toUseCaseRequest(AtualizarConsultaRequest dto);
    
    ConsultaResponse toResponse(Consulta consulta);
    
    List<ConsultaResponse> toResponseList(List<Consulta> consultas);
}
