package br.gov.sus.agendamento.domain.gateway;

import br.gov.sus.shared.domain.entity.Agendamento;
import br.gov.sus.shared.domain.enums.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgendamentoGateway {

    Agendamento salvar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(Long id);

    boolean existeAgendamentoProfissionalNoHorario(
            Long profissionalId, LocalDateTime dataHora, List<StatusAgendamento> statusExcluidos);
}
