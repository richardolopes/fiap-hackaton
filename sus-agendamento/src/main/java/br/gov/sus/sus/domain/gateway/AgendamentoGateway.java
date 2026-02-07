package br.gov.sus.sus.domain.gateway;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.enums.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgendamentoGateway {

    Agendamento salvar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(Long id);

    boolean existeAgendamentoProfissionalNoHorario(
            Long profissionalId, LocalDateTime dataHora, List<StatusAgendamento> statusExcluidos);
}
