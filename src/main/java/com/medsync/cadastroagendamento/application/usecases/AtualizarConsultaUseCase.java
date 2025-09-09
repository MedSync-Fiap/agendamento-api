package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.ConflitoHorarioException;
import com.medsync.cadastroagendamento.application.exceptions.ConsultaNaoEncontradaException;
import com.medsync.cadastroagendamento.application.exceptions.ConsultaNaoPodeSerEditadaException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.events.ConsultaEditadaEvent;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AtualizarConsultaUseCase {
    
    private final ConsultaGateway consultaGateway;
    private final UsuarioGateway usuarioGateway;
    private final RabbitTemplate rabbitTemplate;
    
    public AtualizarConsultaUseCase(ConsultaGateway consultaGateway,
                                   UsuarioGateway usuarioGateway,
                                   RabbitTemplate rabbitTemplate) {
        this.consultaGateway = consultaGateway;
        this.usuarioGateway = usuarioGateway;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public Consulta executar(UUID id, AtualizarConsultaRequest request) {
        Consulta consulta = consultaGateway.buscarPorId(id)
                .orElseThrow(() -> new ConsultaNaoEncontradaException(id));
        
        // Verificar se consulta pode ser editada
        if (!consulta.podeSerEditada()) {
            throw new ConsultaNaoPodeSerEditadaException(id);
        }
        
        Map<String, Object> alteracoes = new HashMap<>();
        
        // Atualizar médico se fornecido
        if (request.medicoId() != null && !request.medicoId().equals(consulta.getMedicoId())) {
            Usuario medico = usuarioGateway.buscarPorId(request.medicoId())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException(request.medicoId()));
            
            alteracoes.put("medico_id", Map.of(
                "valor_anterior", consulta.getMedicoId(),
                "valor_novo", request.medicoId()
            ));
            consulta.setMedicoId(request.medicoId());
        }
        
        // Atualizar data/hora se fornecida
        if (request.dataHora() != null && !request.dataHora().equals(consulta.getDataHora())) {
            // Verificar conflito de horário (excluindo a própria consulta)
            if (consultaGateway.existeConsultaNoHorarioExcluindo(
                    consulta.getMedicoId(), request.dataHora(), id)) {
                throw new ConflitoHorarioException(consulta.getMedicoId(), request.dataHora());
            }
            
            alteracoes.put("data_hora", Map.of(
                "valor_anterior", consulta.getDataHora(),
                "valor_novo", request.dataHora()
            ));
            consulta.atualizarDataHora(request.dataHora());
        }
        
        // Atualizar observações se fornecidas
        if (request.observacoes() != null && !request.observacoes().equals(consulta.getObservacoes())) {
            alteracoes.put("observacoes", Map.of(
                "valor_anterior", consulta.getObservacoes(),
                "valor_novo", request.observacoes()
            ));
            consulta.atualizarObservacoes(request.observacoes());
        }
        
        Consulta consultaAtualizada = consultaGateway.salvar(consulta);
        
        // Publicar eventos se houve alterações
        if (!alteracoes.isEmpty()) {
            ConsultaEditadaEvent evento = new ConsultaEditadaEvent(
                consultaAtualizada.getId(),
                consultaAtualizada.getPacienteId(),
                consultaAtualizada.getMedicoId(),
                request.editadoPorId(),
                alteracoes,
                LocalDateTime.now()
            );
            
            // Enviar para fila de histórico
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CONSULTAS, 
                                         "consulta.editada.historico", evento);
            
            // Enviar para fila de notificações
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CONSULTAS, 
                                         "consulta.editada.notificacao", evento);
        }
        
        return consultaAtualizada;
    }
    
    public record AtualizarConsultaRequest(
        UUID medicoId,
        LocalDateTime dataHora,
        String observacoes,
        UUID editadoPorId
    ) {}
}
