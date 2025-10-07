package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Telefone;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.TelefoneJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TelefoneMapper {
    
    @Mapping(target = "usuario", ignore = true)
    Telefone toDomain(TelefoneJpaEntity jpaEntity);
    
    @Mapping(target = "usuario", ignore = true)
    TelefoneJpaEntity toJpa(Telefone domain);
}
