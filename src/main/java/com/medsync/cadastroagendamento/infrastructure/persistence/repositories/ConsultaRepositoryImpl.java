package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.domain.entities.Consulta;
import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.domain.gateways.ConsultaGateway;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.ConsultaJpaEntity;
import com.medsync.cadastroagendamento.infrastructure.persistence.mappers.ConsultaMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ConsultaRepositoryImpl implements ConsultaGateway {
    
    private final ConsultaJpaRepository jpaRepository;
    private final ConsultaMapper mapper;
    
    public ConsultaRepositoryImpl(ConsultaJpaRepository jpaRepository, ConsultaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Consulta salvar(Consulta consulta) {
        ConsultaJpaEntity jpaEntity = mapper.toJpa(consulta);
        ConsultaJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Consulta> buscarPorId(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<Consulta> buscarPorPaciente(UUID pacienteId) {
        return jpaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Consulta> buscarPorMedico(UUID medicoId) {
        return jpaRepository.findByMedicoId(medicoId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Consulta> buscarPorStatus(StatusConsulta status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Consulta> buscarPorDataHora(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return jpaRepository.findByDataHoraBetween(dataInicio, dataFim)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Consulta> buscarPorPacienteEData(UUID pacienteId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return jpaRepository.findByPacienteIdAndDataHoraBetween(pacienteId, dataInicio, dataFim)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Consulta> buscarPorMedicoEData(UUID medicoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return jpaRepository.findByMedicoIdAndDataHoraBetween(medicoId, dataInicio, dataFim)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Consulta> buscarTodas() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
    
    @Override
    public void deletar(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existeConsultaNoHorario(UUID medicoId, LocalDateTime dataHora) {
        return jpaRepository.existsByMedicoIdAndDataHora(medicoId, dataHora);
    }
    
    @Override
    public boolean existeConsultaNoHorarioExcluindo(UUID medicoId, LocalDateTime dataHora, UUID consultaId) {
        return jpaRepository.existsByMedicoIdAndDataHoraAndIdNot(medicoId, dataHora, consultaId);
    }
}
