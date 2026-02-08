package br.gov.sus.agendamento.domain.usecase.agendamento;

import br.gov.sus.agendamento.application.exception.BusinessException;
import br.gov.sus.shared.domain.entity.*;
import br.gov.sus.shared.domain.enums.StatusAgendamento;
import br.gov.sus.shared.domain.enums.TipoAtendimento;
import br.gov.sus.agendamento.domain.gateway.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use Case para criar agendamento buscando UBS mais próxima do CEP
 */
@Slf4j
@RequiredArgsConstructor
public class CriarAgendamentoPorCepUseCase {

    private final AgendamentoGateway agendamentoGateway;
    private final PacienteGateway pacienteGateway;
    private final ProfissionalGateway profissionalGateway;
    private final UnidadeSaudeGateway unidadeSaudeGateway;
    private final EspecialidadeGateway especialidadeGateway;

    public Agendamento executar(Long pacienteId, String cep, Long especialidadeId,
                                LocalDateTime dataHoraAgendamento, TipoAtendimento tipoAtendimento,
                                String observacoes) {

        log.info("Criando agendamento por CEP: {} para paciente: {}", cep, pacienteId);

        Paciente paciente = pacienteGateway.buscarPorId(pacienteId)
                .orElseThrow(() -> new BusinessException("Paciente não encontrado"));

        Especialidade especialidade = especialidadeGateway.buscarPorId(especialidadeId)
                .orElseThrow(() -> new BusinessException("Especialidade não encontrada"));

        UnidadeSaude unidadeSaude = unidadeSaudeGateway.buscarUbsMaisProximaPorCep(cep)
                .orElseThrow(() -> new BusinessException("Nenhuma UBS encontrada próxima ao CEP: " + cep));

        log.info("UBS encontrada: {} - {}", unidadeSaude.getCodigoCnes(), unidadeSaude.getNome());

        List<Profissional> profissionais = profissionalGateway.buscarPorEspecialidadeId(especialidadeId);

        if (profissionais.isEmpty()) {
            throw new BusinessException("Nenhum profissional encontrado para a especialidade: " + especialidade.getNome());
        }

        Profissional profissional = profissionais.get(0);
        log.info("Profissional selecionado: {} - {}", profissional.getId(), profissional.getNomeCompleto());

        List<StatusAgendamento> statusAtivos = List.of(
                StatusAgendamento.AGENDADO,
                StatusAgendamento.CONFIRMADO
        );

        boolean horarioOcupado = agendamentoGateway.existeAgendamentoProfissionalNoHorario(
                profissional.getId(), dataHoraAgendamento,
                List.of(StatusAgendamento.CANCELADO_PACIENTE, StatusAgendamento.CANCELADO_UNIDADE));

        if (horarioOcupado) {
            throw new BusinessException("Horário não disponível para agendamento");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setProfissional(profissional);
        agendamento.setUnidadeSaude(unidadeSaude);
        agendamento.setEspecialidade(especialidade);
        agendamento.setDataHoraAgendamento(dataHoraAgendamento);
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setTipoAtendimento(tipoAtendimento);
        agendamento.setObservacoes(observacoes);
        agendamento.setDataCriacao(LocalDateTime.now());

        Agendamento agendamentoSalvo = agendamentoGateway.salvar(agendamento);
        log.info("Agendamento criado com sucesso: ID {}", agendamentoSalvo.getId());

        return agendamentoSalvo;
    }
}
