package br.gov.sus.telemedicina.infrastructure.persistence.repository;

import br.gov.sus.telemedicina.infrastructure.persistence.entity.ConsultaTelemedicinaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultaTelemedicinaRepository extends JpaRepository<ConsultaTelemedicinaJpaEntity, Long> {

    Optional<ConsultaTelemedicinaJpaEntity> findByAgendamentoId(Long agendamentoId);

    boolean existsByAgendamentoId(Long agendamentoId);
}

