package com.medsync.cadastroagendamento.presentation.mappers;

import com.medsync.cadastroagendamento.domain.entities.Telefone;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.TipoTelefone;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.TelefoneResponse;
import com.medsync.cadastroagendamento.presentation.dto.UsuarioResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-23T09:15:30-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class UsuarioDtoMapperImpl implements UsuarioDtoMapper {

    @Override
    public AtualizarUsuarioRequest toUseCaseRequest(AtualizarUsuarioRequest dto) {
        if ( dto == null ) {
            return null;
        }

        String nome = null;
        String email = null;
        String senha = null;
        UUID roleId = null;

        nome = dto.nome();
        email = dto.email();
        senha = dto.senha();
        roleId = dto.roleId();

        AtualizarUsuarioRequest atualizarUsuarioRequest = new AtualizarUsuarioRequest( nome, email, senha, roleId );

        return atualizarUsuarioRequest;
    }

    @Override
    public UsuarioResponse toResponse(Usuario usuario) {
        if ( usuario == null ) {
            return null;
        }

        UUID id = null;
        String nome = null;
        String cpf = null;
        String email = null;
        UUID roleId = null;
        boolean ativo = false;
        LocalDateTime criadoEm = null;
        LocalDateTime atualizadoEm = null;
        List<TelefoneResponse> telefones = null;

        id = usuario.getId();
        nome = usuario.getNome();
        cpf = usuario.getCpf();
        email = usuario.getEmail();
        roleId = usuario.getRoleId();
        ativo = usuario.isAtivo();
        criadoEm = usuario.getCriadoEm();
        atualizadoEm = usuario.getAtualizadoEm();
        telefones = telefoneListToTelefoneResponseList( usuario.getTelefones() );

        UsuarioResponse usuarioResponse = new UsuarioResponse( id, nome, cpf, email, roleId, ativo, criadoEm, atualizadoEm, telefones );

        return usuarioResponse;
    }

    @Override
    public List<UsuarioResponse> toResponseList(List<Usuario> usuarios) {
        if ( usuarios == null ) {
            return null;
        }

        List<UsuarioResponse> list = new ArrayList<UsuarioResponse>( usuarios.size() );
        for ( Usuario usuario : usuarios ) {
            list.add( toResponse( usuario ) );
        }

        return list;
    }

    @Override
    public TelefoneResponse toTelefoneResponse(Telefone telefone) {
        if ( telefone == null ) {
            return null;
        }

        UUID id = null;
        String numero = null;
        TipoTelefone tipo = null;

        id = telefone.getId();
        numero = telefone.getNumero();
        tipo = telefone.getTipo();

        TelefoneResponse telefoneResponse = new TelefoneResponse( id, numero, tipo );

        return telefoneResponse;
    }

    protected List<TelefoneResponse> telefoneListToTelefoneResponseList(List<Telefone> list) {
        if ( list == null ) {
            return null;
        }

        List<TelefoneResponse> list1 = new ArrayList<TelefoneResponse>( list.size() );
        for ( Telefone telefone : list ) {
            list1.add( toTelefoneResponse( telefone ) );
        }

        return list1;
    }
}
