package com.medsync.cadastroagendamento.infrastructure.persistence.repositories;

import com.medsync.cadastroagendamento.domain.enums.StatusConsulta;
import com.medsync.cadastroagendamento.infrastructure.persistence.entities.ConsultaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsultaJpaRepository extends JpaRepository<ConsultaJpaEntity, UUID> {
    
    List<ConsultaJpaEntity> findByPacienteId(UUID pacienteId);
    
    List<ConsultaJpaEntity> findByMedicoId(UUID medicoId);
    
    List<ConsultaJpaEntity> findByStatus(StatusConsulta status);
    
    @Query("SELECT c FROM ConsultaJpaEntity c WHERE c.dataHora BETWEEN :dataInicio AND :dataFim")
    List<ConsultaJpaEntity> findByDataHoraBetween(@Param("dataInicio") LocalDateTime dataInicio, 
                                                  @Param("dataFim") LocalDateTime dataFim);
    
    @Query("SELECT c FROM ConsultaJpaEntity c WHERE c.pacienteId = :pacienteId AND c.dataHora BETWEEN :dataInicio AND :dataFim")
    List<ConsultaJpaEntity> findByPacienteIdAndDataHoraBetween(@Param("pacienteId") UUID pacienteId,
                                                               @Param("dataInicio") LocalDateTime dataInicio,
                                                               @Param("dataFim") LocalDateTime dataFim);
    
    @Query("SELECT c FROM ConsultaJpaEntity c WHERE c.medicoId = :medicoId AND c.dataHora BETWEEN :dataInicio AND :dataFim")
    List<ConsultaJpaEntity> findByMedicoIdAndDataHoraBetween(@Param("medicoId") UUID medicoId,
                                                             @Param("dataInicio") LocalDateTime dataInicio,
                                                             @Param("dataFim") LocalDateTime dataFim);
    
    @Query("SELECT COUNT(c) > 0 FROM ConsultaJpaEntity c WHERE c.medicoId = :medicoId AND c.dataHora = :dataHora")
    boolean existsByMedicoIdAndDataHora(@Param("medicoId") UUID medicoId, @Param("dataHora") LocalDateTime dataHora);
    
    @Query("SELECT COUNT(c) > 0 FROM ConsultaJpaEntity c WHERE c.medicoId = :medicoId AND c.dataHora = :dataHora AND c.id != :consultaId")
    boolean existsByMedicoIdAndDataHoraAndIdNot(@Param("medicoId") UUID medicoId, 
                                                @Param("dataHora") LocalDateTime dataHora,
                                                @Param("consultaId") UUID consultaId);
}
