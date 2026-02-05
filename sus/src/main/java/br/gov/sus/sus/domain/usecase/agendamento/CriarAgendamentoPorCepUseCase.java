package br.gov.sus.sus.domain.usecase.agendamento;

import br.gov.sus.sus.application.exception.BusinessException;
import br.gov.sus.sus.domain.entity.*;
import br.gov.sus.sus.domain.enums.StatusAgendamento;
import br.gov.sus.sus.domain.enums.TipoAtendimento;
import br.gov.sus.sus.domain.gateway.*;
import br.gov.sus.sus.infrastructure.client.gateway.UnidadeSaudeClientGateway;
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
    private final UnidadeSaudeClientGateway unidadeSaudeGateway;
    private final EspecialidadeGateway especialidadeGateway;

    public Agendamento executar(Long pacienteId, String cep, Long especialidadeId,
                                 LocalDateTime dataHoraAgendamento, TipoAtendimento tipoAtendimento,
                                 String observacoes) {
        
        log.info("Criando agendamento por CEP: {} para paciente: {}", cep, pacienteId);

        // 1. Validar paciente
        Paciente paciente = pacienteGateway.buscarPorId(pacienteId)
                .orElseThrow(() -> new BusinessException("Paciente não encontrado"));

        // 2. Validar especialidade
        Especialidade especialidade = especialidadeGateway.buscarPorId(especialidadeId)
                .orElseThrow(() -> new BusinessException("Especialidade não encontrada"));

        // 3. Buscar UBS mais próxima do CEP usando API do SUS
        UnidadeSaude unidadeSaude = unidadeSaudeGateway.buscarUbsMaisProximaPorCep(cep)
                .orElseThrow(() -> new BusinessException("Nenhuma UBS encontrada próxima ao CEP: " + cep));

        log.info("UBS encontrada: {} - {}", unidadeSaude.getCodigoCnes(), unidadeSaude.getNome());

        // 4. Buscar profissional da especialidade na unidade (ou qualquer profissional da especialidade)
        List<Profissional> profissionais = profissionalGateway.buscarPorEspecialidadeId(especialidadeId);
        
        if (profissionais.isEmpty()) {
            throw new BusinessException("Nenhum profissional encontrado para a especialidade: " + especialidade.getNome());
        }

        // Selecionar o primeiro profissional disponível
        Profissional profissional = profissionais.get(0);
        log.info("Profissional selecionado: {} - {}", profissional.getId(), profissional.getNomeCompleto());

        // 5. Verificar se já existe agendamento no mesmo horário
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

        // 6. Criar agendamento
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
