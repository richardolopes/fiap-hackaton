package br.gov.sus.agendamento.infrastructure.client.gateway;

import br.gov.sus.agendamento.domain.entity.Paciente;
import br.gov.sus.agendamento.domain.gateway.PacienteGateway;
import br.gov.sus.agendamento.infrastructure.client.PacienteClient;
import br.gov.sus.agendamento.infrastructure.persistence.mapper.PacienteMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PacienteClientGateway implements PacienteGateway {

    private final PacienteClient client;
    private final PacienteMapper mapper;

    public PacienteClientGateway(PacienteClient client, PacienteMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Optional<Paciente> buscarPorId(Long id) {
        try {
            var response = client.buscarPorId(id);
            return Optional.of(mapper.toDomain(response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
