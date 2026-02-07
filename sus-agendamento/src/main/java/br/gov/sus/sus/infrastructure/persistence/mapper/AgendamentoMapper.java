package br.gov.sus.sus.infrastructure.persistence.mapper;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.gateway.EspecialidadeGateway;
import br.gov.sus.sus.domain.gateway.PacienteGateway;
import br.gov.sus.sus.domain.gateway.ProfissionalGateway;
import br.gov.sus.sus.domain.gateway.UnidadeSaudeGateway;
import br.gov.sus.sus.infrastructure.persistence.entity.AgendamentoJpaEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoMapper {

    private final PacienteGateway pacienteGateway;
    private final ProfissionalGateway profissionalGateway;
    private final UnidadeSaudeGateway unidadeSaudeGateway;
    private final EspecialidadeGateway especialidadeGateway;

    public AgendamentoMapper(@Lazy PacienteGateway pacienteGateway,
                             @Lazy ProfissionalGateway profissionalGateway,
                             @Lazy UnidadeSaudeGateway unidadeSaudeGateway,
                             @Lazy EspecialidadeGateway especialidadeGateway) {
        this.pacienteGateway = pacienteGateway;
        this.profissionalGateway = profissionalGateway;
        this.unidadeSaudeGateway = unidadeSaudeGateway;
        this.especialidadeGateway = especialidadeGateway;
    }

    public Agendamento toDomain(AgendamentoJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setId(entity.getId());
        agendamento.setDataHoraAgendamento(entity.getDataHoraAgendamento());
        agendamento.setStatus(entity.getStatus());
        agendamento.setTipoAtendimento(entity.getTipoAtendimento());
        agendamento.setObservacoes(entity.getObservacoes());
        agendamento.setDataCriacao(entity.getDataCriacao());
        agendamento.setDataAtualizacao(entity.getDataAtualizacao());
        agendamento.setMotivoCancelamento(entity.getMotivoCancelamento());

        if (entity.getPacienteId() != null) {
            pacienteGateway.buscarPorId(entity.getPacienteId())
                    .ifPresent(agendamento::setPaciente);
        }

        if (entity.getProfissionalId() != null) {
            profissionalGateway.buscarPorId(entity.getProfissionalId())
                    .ifPresent(agendamento::setProfissional);
        }

        if (entity.getCodigoCnesUnidade() != null) {
            unidadeSaudeGateway.buscarPorCodigoCnes(entity.getCodigoCnesUnidade())
                    .ifPresent(agendamento::setUnidadeSaude);
        }

        if (entity.getEspecialidadeId() != null) {
            especialidadeGateway.buscarPorId(entity.getEspecialidadeId())
                    .ifPresent(agendamento::setEspecialidade);
        }

        return agendamento;
    }

    public AgendamentoJpaEntity toEntity(Agendamento domain) {
        if (domain == null) {
            return null;
        }

        return AgendamentoJpaEntity.builder()
                .id(domain.getId())
                .pacienteId(domain.getPaciente() != null ? domain.getPaciente().getId() : null)
                .profissionalId(domain.getProfissional() != null ? domain.getProfissional().getId() : null)
                .codigoCnesUnidade(domain.getUnidadeSaude() != null ? domain.getUnidadeSaude().getCodigoCnes() : null)
                .especialidadeId(domain.getEspecialidade() != null ? domain.getEspecialidade().getId() : null)
                .dataHoraAgendamento(domain.getDataHoraAgendamento())
                .status(domain.getStatus())
                .tipoAtendimento(domain.getTipoAtendimento())
                .observacoes(domain.getObservacoes())
                .dataCriacao(domain.getDataCriacao())
                .dataAtualizacao(domain.getDataAtualizacao())
                .motivoCancelamento(domain.getMotivoCancelamento())
                .build();
    }
}
