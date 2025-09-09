package com.medsync.cadastroagendamento.domain.gateways;

import com.medsync.cadastroagendamento.domain.entities.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioGateway {
    Usuario salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(UUID id);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorCpf(String cpf);
    List<Usuario> buscarPorRole(UUID roleId);
    List<Usuario> buscarTodos();
    List<Usuario> buscarMedicos();
    List<Usuario> buscarPacientes();
    void deletar(UUID id);
    boolean existePorEmail(String email);
    boolean existePorCpf(String cpf);
}