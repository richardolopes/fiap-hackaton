package br.gov.sus.agendamento.domain.usecase.agendamento;

import br.gov.sus.agendamento.application.exception.ResourceNotFoundException;
import br.gov.sus.agendamento.domain.entity.Agendamento;
import br.gov.sus.agendamento.domain.enums.StatusAgendamento;
import br.gov.sus.agendamento.domain.gateway.AgendamentoGateway;

public class CancelarAgendamentoUseCase {

    private final AgendamentoGateway agendamentoGateway;

    public CancelarAgendamentoUseCase(AgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }

    public Agendamento executar(Long id, String motivo, boolean canceladoPeloPaciente) {
        Agendamento agendamento = agendamentoGateway.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));

        if (agendamento.getStatus() == StatusAgendamento.CONCLUIDO) {
            throw new IllegalArgumentException("Não é possível cancelar um agendamento já concluído");
        }

        agendamento.cancelar(motivo, canceladoPeloPaciente);
        return agendamentoGateway.salvar(agendamento);
    }
}
