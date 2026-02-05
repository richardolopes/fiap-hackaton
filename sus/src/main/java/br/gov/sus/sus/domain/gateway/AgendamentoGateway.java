package br.gov.sus.sus.domain.gateway;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.enums.StatusAgendamento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgendamentoGateway {
    
    Agendamento salvar(Agendamento agendamento);
    
    Optional<Agendamento> buscarPorId(Long id);
    
    List<Agendamento> buscarPorPacienteId(Long pacienteId);
    
    List<Agendamento> buscarPorProfissionalId(Long profissionalId);
    
    List<Agendamento> buscarPorCodigoCnesUnidade(String codigoCnesUnidade);
    
    List<Agendamento> buscarPorPacienteIdEStatus(Long pacienteId, StatusAgendamento status);
    
    List<Agendamento> buscarAgendamentosProfissionalNoPeriodo(
            Long profissionalId, LocalDateTime inicio, LocalDateTime fim);
    
    boolean existeAgendamentoProfissionalNoHorario(
            Long profissionalId, LocalDateTime dataHora, List<StatusAgendamento> statusExcluidos);
    
    void deletar(Long id);
}
