package br.gov.sus.sus.domain.usecase.agendamento;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.gateway.AgendamentoGateway;

import java.util.List;

public class BuscarAgendamentosPorPacienteUseCase {
    
    private final AgendamentoGateway agendamentoGateway;
    
    public BuscarAgendamentosPorPacienteUseCase(AgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }
    
    public List<Agendamento> executar(Long pacienteId) {
        return agendamentoGateway.buscarPorPacienteId(pacienteId);
    }
}
