package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.ConsultaJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-16T21:53:26-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class ConsultaMapperImpl implements ConsultaMapper {

    @Override
    public Consulta toDomain(ConsultaJpaEntity jpaEntity) {
        if ( jpaEntity == null ) {
            return null;
        }

        Consulta consulta = new Consulta();

        consulta.setId( jpaEntity.getId() );
        consulta.setPacienteId( jpaEntity.getPacienteId() );
        consulta.setMedicoId( jpaEntity.getMedicoId() );
        consulta.setCriadoPorId( jpaEntity.getCriadoPorId() );
        consulta.setDataHora( jpaEntity.getDataHora() );
        consulta.setStatus( jpaEntity.getStatus() );
        consulta.setObservacoes( jpaEntity.getObservacoes() );
        consulta.setCriadoEm( jpaEntity.getCriadoEm() );
        consulta.setAtualizadoEm( jpaEntity.getAtualizadoEm() );

        return consulta;
    }

    @Override
    public ConsultaJpaEntity toJpa(Consulta domain) {
        if ( domain == null ) {
            return null;
        }

        ConsultaJpaEntity consultaJpaEntity = new ConsultaJpaEntity();

        consultaJpaEntity.setId( domain.getId() );
        consultaJpaEntity.setPacienteId( domain.getPacienteId() );
        consultaJpaEntity.setMedicoId( domain.getMedicoId() );
        consultaJpaEntity.setCriadoPorId( domain.getCriadoPorId() );
        consultaJpaEntity.setDataHora( domain.getDataHora() );
        consultaJpaEntity.setStatus( domain.getStatus() );
        consultaJpaEntity.setObservacoes( domain.getObservacoes() );
        consultaJpaEntity.setCriadoEm( domain.getCriadoEm() );
        consultaJpaEntity.setAtualizadoEm( domain.getAtualizadoEm() );

        return consultaJpaEntity;
    }
}
