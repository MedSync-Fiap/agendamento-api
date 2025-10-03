package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Especialidade;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.EspecialidadeJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class EspecialidadeMapper {
    
    public Especialidade toDomain(EspecialidadeJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }
        
        return new Especialidade(
            jpaEntity.getId(),
            jpaEntity.getNome(),
            jpaEntity.getDescricao(),
            jpaEntity.getCriadoEm(),
            jpaEntity.getAtualizadoEm()
        );
    }
    
    public EspecialidadeJpaEntity toJpa(Especialidade domain) {
        if (domain == null) {
            return null;
        }
        
        EspecialidadeJpaEntity jpaEntity = new EspecialidadeJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setNome(domain.getNome());
        jpaEntity.setDescricao(domain.getDescricao());
        jpaEntity.setCriadoEm(domain.getCriadoEm());
        jpaEntity.setAtualizadoEm(domain.getAtualizadoEm());
        
        return jpaEntity;
    }
}
