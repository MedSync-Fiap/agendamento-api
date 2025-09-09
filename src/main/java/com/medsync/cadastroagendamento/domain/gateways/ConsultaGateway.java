package com.medsync.cadastroagendamento.domain.gateways;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultaGateway {
    Consulta salvar(Consulta consulta);
    Optional<Consulta> buscarPorId(UUID id);
    List<Consulta> buscarPorPaciente(UUID pacienteId);
    List<Consulta> buscarPorMedico(UUID medicoId);
    List<Consulta> buscarPorStatus(StatusConsulta status);
    List<Consulta> buscarPorDataHora(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Consulta> buscarPorPacienteEData(UUID pacienteId, LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Consulta> buscarPorMedicoEData(UUID medicoId, LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Consulta> buscarTodas();
    void deletar(UUID id);
    boolean existeConsultaNoHorario(UUID medicoId, LocalDateTime dataHora);
    boolean existeConsultaNoHorarioExcluindo(UUID medicoId, LocalDateTime dataHora, UUID consultaId);
}
