package br.gov.sus.agendamento.infrastructure.client.gateway;

import br.gov.sus.shared.domain.entity.Profissional;
import br.gov.sus.agendamento.domain.gateway.ProfissionalGateway;
import br.gov.sus.agendamento.infrastructure.client.ProfissionalClient;
import br.gov.sus.agendamento.infrastructure.persistence.mapper.ProfissionalMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProfissionalClientGateway implements ProfissionalGateway {

    private final ProfissionalClient client;
    private final ProfissionalMapper mapper;

    public ProfissionalClientGateway(ProfissionalClient client, ProfissionalMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Optional<Profissional> buscarPorId(Long id) {
        try {
            var response = client.buscarPorId(id);
            return Optional.of(mapper.toDomain(response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Profissional> buscarPorEspecialidadeId(Long especialidadeId) {
        try {
            return client.buscarPorEspecialidade(especialidadeId).stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
}
