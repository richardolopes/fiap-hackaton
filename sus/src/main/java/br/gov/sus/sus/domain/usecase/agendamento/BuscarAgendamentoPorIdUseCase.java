package br.gov.sus.sus.domain.usecase.agendamento;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.gateway.AgendamentoGateway;

public class BuscarAgendamentoPorIdUseCase {
    
    private final AgendamentoGateway agendamentoGateway;
    
    public BuscarAgendamentoPorIdUseCase(AgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }
    
    public Agendamento executar(Long id) {
        return agendamentoGateway.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
    }
}
