package br.gov.sus.sus.domain.usecase.agendamento;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.entity.Especialidade;
import br.gov.sus.sus.domain.entity.Paciente;
import br.gov.sus.sus.domain.entity.Profissional;
import br.gov.sus.sus.domain.entity.UnidadeSaude;
import br.gov.sus.sus.domain.enums.StatusAgendamento;
import br.gov.sus.sus.domain.enums.TipoAtendimento;
import br.gov.sus.sus.domain.gateway.AgendamentoGateway;
import br.gov.sus.sus.domain.gateway.EspecialidadeGateway;
import br.gov.sus.sus.domain.gateway.PacienteGateway;
import br.gov.sus.sus.domain.gateway.ProfissionalGateway;
import br.gov.sus.sus.domain.gateway.UnidadeSaudeGateway;

import java.time.LocalDateTime;
import java.util.List;

public class CriarAgendamentoUseCase {
    
    private final AgendamentoGateway agendamentoGateway;
    private final PacienteGateway pacienteGateway;
    private final ProfissionalGateway profissionalGateway;
    private final UnidadeSaudeGateway unidadeSaudeGateway;
    private final EspecialidadeGateway especialidadeGateway;
    
    public CriarAgendamentoUseCase(AgendamentoGateway agendamentoGateway,
                                   PacienteGateway pacienteGateway,
                                   ProfissionalGateway profissionalGateway,
                                   UnidadeSaudeGateway unidadeSaudeGateway,
                                   EspecialidadeGateway especialidadeGateway) {
        this.agendamentoGateway = agendamentoGateway;
        this.pacienteGateway = pacienteGateway;
        this.profissionalGateway = profissionalGateway;
        this.unidadeSaudeGateway = unidadeSaudeGateway;
        this.especialidadeGateway = especialidadeGateway;
    }
    
    public Agendamento executar(Long pacienteId, Long profissionalId, String codigoCnesUnidade,
                                Long especialidadeId, LocalDateTime dataHoraAgendamento,
                                TipoAtendimento tipoAtendimento, String observacoes) {
        
        // Buscar entidades
        Paciente paciente = pacienteGateway.buscarPorId(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        
        Profissional profissional = profissionalGateway.buscarPorId(profissionalId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));
        
        UnidadeSaude unidadeSaude = unidadeSaudeGateway.buscarPorCodigoCnes(codigoCnesUnidade)
                .orElseThrow(() -> new IllegalArgumentException("Unidade de Saúde não encontrada"));
        
        Especialidade especialidade = especialidadeGateway.buscarPorId(especialidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade não encontrada"));
        
        // Verificar se o horário está disponível
        List<StatusAgendamento> statusExcluidos = List.of(
                StatusAgendamento.CANCELADO_PACIENTE,
                StatusAgendamento.CANCELADO_UNIDADE);
        
        if (agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                profissionalId, dataHoraAgendamento, statusExcluidos)) {
            throw new IllegalArgumentException("Horário não disponível para agendamento");
        }
        
        // Verificar se o paciente já tem agendamento no mesmo horário
        List<Agendamento> agendamentosPaciente = agendamentoGateway
                .buscarPorPacienteIdEStatus(pacienteId, StatusAgendamento.AGENDADO);
        
        boolean conflito = agendamentosPaciente.stream()
                .anyMatch(a -> a.getDataHoraAgendamento().equals(dataHoraAgendamento));
        
        if (conflito) {
            throw new IllegalArgumentException("Paciente já possui agendamento neste horário");
        }
        
        // Criar agendamento
        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setProfissional(profissional);
        agendamento.setUnidadeSaude(unidadeSaude);
        agendamento.setEspecialidade(especialidade);
        agendamento.setDataHoraAgendamento(dataHoraAgendamento);
        agendamento.setTipoAtendimento(tipoAtendimento);
        agendamento.setObservacoes(observacoes);
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setDataCriacao(LocalDateTime.now());
        
        return agendamentoGateway.salvar(agendamento);
    }
}
