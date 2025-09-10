package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.ConsultaNaoEncontradaException;
import com.medsync.cadastroagendamento.application.exceptions.ConsultaNaoPodeSerEditadaException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.presentation.dto.AtualizarConsultaRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AtualizarConsultaUseCase {
    
    private final ConsultaGateway consultaGateway;
    private final UsuarioGateway usuarioGateway;
    private final ValidarConsultaUseCase validarConsultaUseCase;
    private final PublicarEventoConsultaUseCase publicarEventoConsultaUseCase;
    
    public AtualizarConsultaUseCase(ConsultaGateway consultaGateway,
                                   UsuarioGateway usuarioGateway,
                                   ValidarConsultaUseCase validarConsultaUseCase,
                                   PublicarEventoConsultaUseCase publicarEventoConsultaUseCase) {
        this.consultaGateway = consultaGateway;
        this.usuarioGateway = usuarioGateway;
        this.validarConsultaUseCase = validarConsultaUseCase;
        this.publicarEventoConsultaUseCase = publicarEventoConsultaUseCase;
    }
    
    public Consulta executar(UUID id, AtualizarConsultaRequest request) {
        Consulta consulta = buscarConsulta(id);
        validarConsultaPodeSerEditada(consulta);
        
        Map<String, Object> alteracoes = new HashMap<>();
        
        if (request.medicoId() != null && !request.medicoId().equals(consulta.getMedicoId())) {
            validarMedicoExiste(request.medicoId());
            alteracoes.put("medico_id", Map.of(
                "valor_anterior", consulta.getMedicoId(),
                "valor_novo", request.medicoId()
            ));
            consulta.setMedicoId(request.medicoId());
        }
        
        if (request.dataHora() != null && !request.dataHora().equals(consulta.getDataHora())) {
            UUID medicoIdParaValidacao = request.medicoId() != null ? request.medicoId() : consulta.getMedicoId();
            validarConsultaUseCase.validarAtualizacaoConsulta(id, medicoIdParaValidacao, request.dataHora());
            alteracoes.put("data_hora", Map.of(
                "valor_anterior", consulta.getDataHora(),
                "valor_novo", request.dataHora()
            ));
            consulta.atualizarDataHora(request.dataHora());
        }
        
        if (request.observacoes() != null && !request.observacoes().equals(consulta.getObservacoes())) {
            alteracoes.put("observacoes", Map.of(
                "valor_anterior", consulta.getObservacoes(),
                "valor_novo", request.observacoes()
            ));
            consulta.atualizarObservacoes(request.observacoes());
        }
        
        Consulta consultaAtualizada = consultaGateway.salvar(consulta);
        
        if (!alteracoes.isEmpty()) {
            publicarEventoConsultaUseCase.publicarConsultaEditada(consultaAtualizada, request.editadoPorId(), alteracoes);
        }
        
        return consultaAtualizada;
    }
    
    private Consulta buscarConsulta(UUID id) {
        return consultaGateway.buscarPorId(id)
                .orElseThrow(() -> new ConsultaNaoEncontradaException(id));
    }
    
    private void validarConsultaPodeSerEditada(Consulta consulta) {
        if (!consulta.podeSerEditada()) {
            throw new ConsultaNaoPodeSerEditadaException(consulta.getId());
        }
    }
    
    private void validarMedicoExiste(UUID medicoId) {
        Usuario medico = usuarioGateway.buscarPorId(medicoId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(medicoId));
    }
}
