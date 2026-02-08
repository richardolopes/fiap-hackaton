package br.gov.sus.telemedicina.infrastructure.persistence.repository;

import br.gov.sus.telemedicina.domain.enums.StatusAgendamento;
import br.gov.sus.telemedicina.domain.enums.TipoAtendimento;
import br.gov.sus.telemedicina.infrastructure.persistence.entity.AgendamentoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<AgendamentoJpaEntity, Long> {

    @Query("""
        SELECT a FROM AgendamentoJpaEntity a 
        WHERE a.tipoAtendimento = :tipoAtendimento
        AND a.status IN :statusList
        AND a.dataHoraAgendamento BETWEEN :dataInicio AND :dataFim
        ORDER BY a.dataHoraAgendamento ASC
        """)
    List<AgendamentoJpaEntity> findAgendamentosParaTelemedicina(
            @Param("tipoAtendimento") TipoAtendimento tipoAtendimento,
            @Param("statusList") List<StatusAgendamento> statusList,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}

