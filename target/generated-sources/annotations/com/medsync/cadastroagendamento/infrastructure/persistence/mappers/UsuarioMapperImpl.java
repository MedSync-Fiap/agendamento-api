package com.medsync.cadastroagendamento.infrastructure.persistence.mappers;

import com.medsync.cadastroagendamento.domain.entities.Telefone;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.TelefoneJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.UsuarioJpaEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-09T20:03:56-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class UsuarioMapperImpl implements UsuarioMapper {

    @Override
    public Usuario toDomain(UsuarioJpaEntity jpaEntity) {
        if ( jpaEntity == null ) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setTelefones( telefoneJpaToTelefone( jpaEntity.getTelefones() ) );
        usuario.setId( jpaEntity.getId() );
        usuario.setNome( jpaEntity.getNome() );
        usuario.setCpf( jpaEntity.getCpf() );
        usuario.setEmail( jpaEntity.getEmail() );
        usuario.setSenhaHash( jpaEntity.getSenhaHash() );
        usuario.setRoleId( jpaEntity.getRoleId() );
        usuario.setAtivo( jpaEntity.isAtivo() );
        usuario.setCriadoEm( jpaEntity.getCriadoEm() );
        usuario.setAtualizadoEm( jpaEntity.getAtualizadoEm() );

        return usuario;
    }

    @Override
    public UsuarioJpaEntity toJpa(Usuario domain) {
        if ( domain == null ) {
            return null;
        }

        UsuarioJpaEntity usuarioJpaEntity = new UsuarioJpaEntity();

        usuarioJpaEntity.setTelefones( telefoneToTelefoneJpa( domain.getTelefones() ) );
        usuarioJpaEntity.setId( domain.getId() );
        usuarioJpaEntity.setNome( domain.getNome() );
        usuarioJpaEntity.setCpf( domain.getCpf() );
        usuarioJpaEntity.setEmail( domain.getEmail() );
        usuarioJpaEntity.setSenhaHash( domain.getSenhaHash() );
        usuarioJpaEntity.setRoleId( domain.getRoleId() );
        usuarioJpaEntity.setAtivo( domain.isAtivo() );
        usuarioJpaEntity.setCriadoEm( domain.getCriadoEm() );
        usuarioJpaEntity.setAtualizadoEm( domain.getAtualizadoEm() );

        return usuarioJpaEntity;
    }

    @Override
    public Telefone telefoneJpaToTelefone(TelefoneJpaEntity telefoneJpa) {
        if ( telefoneJpa == null ) {
            return null;
        }

        Telefone telefone = new Telefone();

        telefone.setId( telefoneJpa.getId() );
        telefone.setUsuarioId( telefoneJpa.getUsuarioId() );
        telefone.setNumero( telefoneJpa.getNumero() );
        telefone.setTipo( telefoneJpa.getTipo() );

        return telefone;
    }

    @Override
    public TelefoneJpaEntity telefoneToTelefoneJpa(Telefone telefone) {
        if ( telefone == null ) {
            return null;
        }

        TelefoneJpaEntity telefoneJpaEntity = new TelefoneJpaEntity();

        telefoneJpaEntity.setId( telefone.getId() );
        telefoneJpaEntity.setUsuarioId( telefone.getUsuarioId() );
        telefoneJpaEntity.setNumero( telefone.getNumero() );
        telefoneJpaEntity.setTipo( telefone.getTipo() );

        return telefoneJpaEntity;
    }
}
