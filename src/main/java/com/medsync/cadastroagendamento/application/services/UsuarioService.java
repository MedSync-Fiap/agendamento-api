package com.medsync.cadastroagendamento.application.services;

import com.medsync.cadastroagendamento.application.usecases.*;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarUsuarioRequest;
import com.medsync.cadastroagendamento.presentation.dto.CriarUsuarioRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {
    
    private final CriarUsuarioUseCase criarUsuarioUseCase;
    private final BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase;
    private final BuscarTodosUsuariosUseCase buscarTodosUsuariosUseCase;
    private final BuscarMedicosUseCase buscarMedicosUseCase;
    private final BuscarPacientesUseCase buscarPacientesUseCase;
    private final AtualizarUsuarioUseCase atualizarUsuarioUseCase;
    
    public UsuarioService(CriarUsuarioUseCase criarUsuarioUseCase,
                         BuscarUsuarioPorIdUseCase buscarUsuarioPorIdUseCase,
                         BuscarTodosUsuariosUseCase buscarTodosUsuariosUseCase,
                         BuscarMedicosUseCase buscarMedicosUseCase,
                         BuscarPacientesUseCase buscarPacientesUseCase,
                         AtualizarUsuarioUseCase atualizarUsuarioUseCase) {
        this.criarUsuarioUseCase = criarUsuarioUseCase;
        this.buscarUsuarioPorIdUseCase = buscarUsuarioPorIdUseCase;
        this.buscarTodosUsuariosUseCase = buscarTodosUsuariosUseCase;
        this.buscarMedicosUseCase = buscarMedicosUseCase;
        this.buscarPacientesUseCase = buscarPacientesUseCase;
        this.atualizarUsuarioUseCase = atualizarUsuarioUseCase;
    }
    
    public Usuario criarUsuario(CriarUsuarioRequest request) {
        return criarUsuarioUseCase.executar(request);
    }
    
    public Usuario buscarPorId(UUID id) {
        return buscarUsuarioPorIdUseCase.executar(id);
    }
    
    public List<Usuario> buscarTodos() {
        return buscarTodosUsuariosUseCase.executar();
    }
    
    public List<Usuario> buscarMedicos() {
        return buscarMedicosUseCase.executar();
    }
    
    public List<Usuario> buscarPacientes() {
        return buscarPacientesUseCase.executar();
    }
    
    public Usuario atualizarUsuario(UUID id, AtualizarUsuarioRequest request) {
        return atualizarUsuarioUseCase.executar(id, request);
    }
}
