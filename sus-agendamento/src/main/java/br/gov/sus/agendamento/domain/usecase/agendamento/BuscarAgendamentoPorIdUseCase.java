package br.gov.sus.agendamento.domain.usecase.agendamento;

import br.gov.sus.agendamento.application.exception.ResourceNotFoundException;
import br.gov.sus.agendamento.domain.entity.Agendamento;
import br.gov.sus.agendamento.domain.gateway.AgendamentoGateway;

public class BuscarAgendamentoPorIdUseCase {

    private final AgendamentoGateway agendamentoGateway;

    public BuscarAgendamentoPorIdUseCase(AgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }

    public Agendamento executar(Long id) {
        return agendamentoGateway.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
    }
}
