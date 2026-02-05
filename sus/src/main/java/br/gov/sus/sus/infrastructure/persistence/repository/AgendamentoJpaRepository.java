package br.gov.sus.sus.infrastructure.persistence.repository;

import br.gov.sus.sus.domain.enums.StatusAgendamento;
import br.gov.sus.sus.infrastructure.persistence.entity.AgendamentoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoJpaRepository extends JpaRepository<AgendamentoJpaEntity, Long> {
    
    List<AgendamentoJpaEntity> findByPacienteId(Long pacienteId);
    
    List<AgendamentoJpaEntity> findByProfissionalId(Long profissionalId);
    
    List<AgendamentoJpaEntity> findByCodigoCnesUnidade(String codigoCnesUnidade);
    
    List<AgendamentoJpaEntity> findByDataHoraAgendamentoBetween(LocalDateTime inicio, LocalDateTime fim);
    
    List<AgendamentoJpaEntity> findByPacienteIdAndStatus(Long pacienteId, StatusAgendamento status);
    
    List<AgendamentoJpaEntity> findByProfissionalIdAndDataHoraAgendamento(Long profissionalId, LocalDateTime dataHora);
}
