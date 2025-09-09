package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Telefone;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.TipoTelefone;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.TelefoneJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    
    @Mapping(target = "telefones", source = "telefones", qualifiedByName = "telefoneJpaToTelefone")
    Usuario toDomain(UsuarioJpaEntity jpaEntity);
    
    @Mapping(target = "telefones", source = "telefones", qualifiedByName = "telefoneToTelefoneJpa")
    UsuarioJpaEntity toJpa(Usuario domain);
    
    @Named("telefoneJpaToTelefone")
    default List<Telefone> telefoneJpaToTelefone(List<TelefoneJpaEntity> telefonesJpa) {
        if (telefonesJpa == null) {
            return null;
        }
        return telefonesJpa.stream()
                .map(this::telefoneJpaToTelefone)
                .toList();
    }
    
    @Named("telefoneToTelefoneJpa")
    default List<TelefoneJpaEntity> telefoneToTelefoneJpa(List<Telefone> telefones) {
        if (telefones == null) {
            return null;
        }
        return telefones.stream()
                .map(this::telefoneToTelefoneJpa)
                .toList();
    }
    
    Telefone telefoneJpaToTelefone(TelefoneJpaEntity telefoneJpa);
    
    TelefoneJpaEntity telefoneToTelefoneJpa(Telefone telefone);
}
