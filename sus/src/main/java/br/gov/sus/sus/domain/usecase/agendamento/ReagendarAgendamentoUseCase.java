package br.gov.sus.sus.domain.usecase.agendamento;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.enums.StatusAgendamento;
import br.gov.sus.sus.domain.gateway.AgendamentoGateway;

import java.time.LocalDateTime;
import java.util.List;

public class ReagendarAgendamentoUseCase {
    
    private final AgendamentoGateway agendamentoGateway;
    
    public ReagendarAgendamentoUseCase(AgendamentoGateway agendamentoGateway) {
        this.agendamentoGateway = agendamentoGateway;
    }
    
    public Agendamento executar(Long id, LocalDateTime novaDataHora) {
        Agendamento agendamento = agendamentoGateway.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        
        // Verificar se o novo horário está disponível
        List<StatusAgendamento> statusExcluidos = List.of(
                StatusAgendamento.CANCELADO_PACIENTE,
                StatusAgendamento.CANCELADO_UNIDADE);
        
        if (agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                agendamento.getProfissional().getId(), novaDataHora, statusExcluidos)) {
            throw new IllegalArgumentException("Novo horário não disponível");
        }
        
        agendamento.reagendar(novaDataHora);
        return agendamentoGateway.salvar(agendamento);
    }
}
