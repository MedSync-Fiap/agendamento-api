package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.ConsultaJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsultaMapper {
    
    Consulta toDomain(ConsultaJpaEntity jpaEntity);
    
    ConsultaJpaEntity toJpa(Consulta domain);
}
