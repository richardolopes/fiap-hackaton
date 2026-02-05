package br.gov.sus.sus.domain.usecase.agendamento;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.gateway.AgendamentoGateway;

import java.util.List;

public class BuscarAgendamentosPorUnidadeUseCase {
    
    private final AgendamentoGateway agendamentoGateway;
    
    public BuscarAgendamentosPorUnidadeUseCase(AgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }
    
    public List<Agendamento> executar(String codigoCnesUnidade) {
        return agendamentoGateway.buscarPorCodigoCnesUnidade(codigoCnesUnidade);
    }
}
