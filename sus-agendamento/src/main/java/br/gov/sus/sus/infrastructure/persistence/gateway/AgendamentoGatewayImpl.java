package br.gov.sus.sus.infrastructure.persistence.gateway;

import br.gov.sus.sus.domain.entity.Agendamento;
import br.gov.sus.sus.domain.gateway.AgendamentoGateway;
import br.gov.sus.sus.infrastructure.persistence.entity.AgendamentoJpaEntity;
import br.gov.sus.sus.infrastructure.persistence.mapper.AgendamentoMapper;
import br.gov.sus.sus.infrastructure.persistence.repository.AgendamentoJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class AgendamentoGatewayImpl implements AgendamentoGateway {

    private final AgendamentoJpaRepository repository;
    private final AgendamentoMapper mapper;

    public AgendamentoGatewayImpl(AgendamentoJpaRepository repository,
                                  AgendamentoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        AgendamentoJpaEntity entity = AgendamentoJpaEntity.builder()
                .id(agendamento.getId())
                .pacienteId(agendamento.getPaciente() != null ? agendamento.getPaciente().getId() : null)
                .profissionalId(agendamento.getProfissional() != null ? agendamento.getProfissional().getId() : null)
                .codigoCnesUnidade(agendamento.getUnidadeSaude() != null ? agendamento.getUnidadeSaude().getCodigoCnes() : null)
                .especialidadeId(agendamento.getEspecialidade() != null ? agendamento.getEspecialidade().getId() : null)
                .dataHoraAgendamento(agendamento.getDataHoraAgendamento())
                .status(agendamento.getStatus())
                .tipoAtendimento(agendamento.getTipoAtendimento())
                .observacoes(agendamento.getObservacoes())
                .dataCriacao(agendamento.getDataCriacao())
                .dataAtualizacao(agendamento.getDataAtualizacao())
                .motivoCancelamento(agendamento.getMotivoCancelamento())
                .build();

        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Agendamento> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existeAgendamentoProfissionalNoHorario(
            Long profissionalId, LocalDateTime dataHora, List<br.gov.sus.sus.domain.enums.StatusAgendamento> statusExcluidos) {
        return repository.findByProfissionalIdAndDataHoraAgendamento(profissionalId, dataHora)
                .stream()
                .anyMatch(a -> !statusExcluidos.contains(a.getStatus()));
    }
}
