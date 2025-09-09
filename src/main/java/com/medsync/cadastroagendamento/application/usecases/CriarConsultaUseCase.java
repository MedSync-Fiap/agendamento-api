package com.medsync.cadastroagendamento.application.usecases;

import com.medsync.cadastroagendamento.application.exceptions.ConflitoHorarioException;
import com.medsync.cadastroagendamento.application.exceptions.UsuarioNaoEncontradoException;
import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.entities.Usuario;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.domain.events.ConsultaCriadaEvent;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import com.medsync.cadastroagendamento.domain.gateways.UsuarioGateway;
import com.medsync.cadastroagendamento.infrastructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CriarConsultaUseCase {
    
    private final ConsultaGateway consultaGateway;
    private final UsuarioGateway usuarioGateway;
    private final RabbitTemplate rabbitTemplate;
    
    public CriarConsultaUseCase(ConsultaGateway consultaGateway, 
                                UsuarioGateway usuarioGateway,
                                RabbitTemplate rabbitTemplate) {
        this.consultaGateway = consultaGateway;
        this.usuarioGateway = usuarioGateway;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public Consulta executar(CriarConsultaRequest request) {
        // Validar se paciente existe
        Usuario paciente = usuarioGateway.buscarPorId(request.pacienteId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(request.pacienteId()));
        
        // Validar se médico existe
        Usuario medico = usuarioGateway.buscarPorId(request.medicoId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(request.medicoId()));
        
        // Validar se usuário que está criando existe
        Usuario criadoPor = usuarioGateway.buscarPorId(request.criadoPorId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(request.criadoPorId()));
        
        // Verificar conflito de horário
        if (consultaGateway.existeConsultaNoHorario(request.medicoId(), request.dataHora())) {
            throw new ConflitoHorarioException(request.medicoId(), request.dataHora());
        }
        
        // Criar consulta
        Consulta consulta = new Consulta();
        consulta.setId(UUID.randomUUID());
        consulta.setPacienteId(request.pacienteId());
        consulta.setMedicoId(request.medicoId());
        consulta.setCriadoPorId(request.criadoPorId());
        consulta.setDataHora(request.dataHora());
        consulta.setStatus(StatusConsulta.AGENDADA);
        consulta.setObservacoes(request.observacoes());
        consulta.setCriadoEm(LocalDateTime.now());
        consulta.setAtualizadoEm(LocalDateTime.now());
        
        Consulta consultaSalva = consultaGateway.salvar(consulta);
        
        // Publicar eventos para RabbitMQ
        ConsultaCriadaEvent evento = new ConsultaCriadaEvent(
            consultaSalva.getId(),
            consultaSalva.getPacienteId(),
            consultaSalva.getMedicoId(),
            consultaSalva.getCriadoPorId(),
            consultaSalva.getDataHora(),
            LocalDateTime.now()
        );
        
        // Enviar para fila de histórico
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CONSULTAS, 
                                     "consulta.criada.historico", evento);
        
        // Enviar para fila de notificações
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_CONSULTAS, 
                                     "consulta.criada.notificacao", evento);
        
        return consultaSalva;
    }
    
    public record CriarConsultaRequest(
        UUID pacienteId,
        UUID medicoId,
        UUID criadoPorId,
        LocalDateTime dataHora,
        String observacoes
    ) {}
}
