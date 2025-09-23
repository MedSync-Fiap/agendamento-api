package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Telefone;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.TelefoneJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TelefoneMapper {
    
    Telefone toDomain(TelefoneJpaEntity jpaEntity);
    
    TelefoneJpaEntity toJpa(Telefone domain);
}
