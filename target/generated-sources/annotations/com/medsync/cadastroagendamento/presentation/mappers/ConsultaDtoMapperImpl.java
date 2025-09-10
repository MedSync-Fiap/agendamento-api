package com.medsync.cadastroagendamento.presentation.mappers;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
import com.medsync.cadastroagendamento.presentation.dto.ConsultaResponse;
import com.medsync.cadastroagendamento.presentation.dto.CriarConsultaRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-09T22:23:07-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class ConsultaDtoMapperImpl implements ConsultaDtoMapper {

    @Override
    public CriarConsultaRequest toUseCaseRequest(CriarConsultaRequest dto) {
        if ( dto == null ) {
            return null;
        }

        UUID pacienteId = null;
        UUID medicoId = null;
        UUID criadoPorId = null;
        LocalDateTime dataHora = null;
        String observacoes = null;

        pacienteId = dto.pacienteId();
        medicoId = dto.medicoId();
        criadoPorId = dto.criadoPorId();
        dataHora = dto.dataHora();
        observacoes = dto.observacoes();

        CriarConsultaRequest criarConsultaRequest = new CriarConsultaRequest( pacienteId, medicoId, criadoPorId, dataHora, observacoes );

        return criarConsultaRequest;
    }

    @Override
    public AtualizarConsultaRequest toUseCaseRequest(AtualizarConsultaRequest dto) {
        if ( dto == null ) {
            return null;
        }

        UUID medicoId = null;
        LocalDateTime dataHora = null;
        String observacoes = null;
        UUID editadoPorId = null;

        medicoId = dto.medicoId();
        dataHora = dto.dataHora();
        observacoes = dto.observacoes();
        editadoPorId = dto.editadoPorId();

        AtualizarConsultaRequest atualizarConsultaRequest = new AtualizarConsultaRequest( medicoId, dataHora, observacoes, editadoPorId );

        return atualizarConsultaRequest;
    }

    @Override
    public ConsultaResponse toResponse(Consulta consulta) {
        if ( consulta == null ) {
            return null;
        }

        UUID id = null;
        UUID pacienteId = null;
        UUID medicoId = null;
        UUID criadoPorId = null;
        LocalDateTime dataHora = null;
        StatusConsulta status = null;
        String observacoes = null;
        LocalDateTime criadoEm = null;
        LocalDateTime atualizadoEm = null;

        id = consulta.getId();
        pacienteId = consulta.getPacienteId();
        medicoId = consulta.getMedicoId();
        criadoPorId = consulta.getCriadoPorId();
        dataHora = consulta.getDataHora();
        status = consulta.getStatus();
        observacoes = consulta.getObservacoes();
        criadoEm = consulta.getCriadoEm();
        atualizadoEm = consulta.getAtualizadoEm();

        ConsultaResponse consultaResponse = new ConsultaResponse( id, pacienteId, medicoId, criadoPorId, dataHora, status, observacoes, criadoEm, atualizadoEm );

        return consultaResponse;
    }

    @Override
    public List<ConsultaResponse> toResponseList(List<Consulta> consultas) {
        if ( consultas == null ) {
            return null;
        }

        List<ConsultaResponse> list = new ArrayList<ConsultaResponse>( consultas.size() );
        for ( Consulta consulta : consultas ) {
            list.add( toResponse( consulta ) );
        }

        return list;
    }
}
