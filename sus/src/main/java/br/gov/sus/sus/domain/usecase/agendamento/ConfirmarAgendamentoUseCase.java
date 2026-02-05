package br.gov.sus.sus.domain.usecase.agendamento;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.enums.StatusAgendamento;
import br.gov.sus.sus.domain.gateway.AgendamentoGateway;

public class ConfirmarAgendamentoUseCase {
    
    private final AgendamentoGateway agendamentoGateway;
    
    public ConfirmarAgendamentoUseCase(AgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }
    
    public Agendamento executar(Long id) {
        Agendamento agendamento = agendamentoGateway.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        
        if (agendamento.getStatus() != StatusAgendamento.AGENDADO) {
            throw new IllegalArgumentException("Apenas agendamentos com status AGENDADO podem ser confirmados");
        }
        
        agendamento.confirmar();
        return agendamentoGateway.salvar(agendamento);
    }
}
