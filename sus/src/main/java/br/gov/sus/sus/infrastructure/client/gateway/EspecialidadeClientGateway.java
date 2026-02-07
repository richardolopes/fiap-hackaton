package br.gov.sus.sus.infrastructure.client.gateway;

import br.gov.sus.sus.domain.entity.Especialidade;
import br.gov.sus.sus.domain.gateway.EspecialidadeGateway;
import br.gov.sus.sus.infrastructure.client.EspecialidadeClient;
import br.gov.sus.sus.infrastructure.persistence.mapper.EspecialidadeMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EspecialidadeClientGateway implements EspecialidadeGateway {
    
    private final EspecialidadeClient client;
    private final EspecialidadeMapper mapper;
    
    public EspecialidadeClientGateway(EspecialidadeClient client, EspecialidadeMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }
    
    @Override
    public Optional<Especialidade> buscarPorId(Long id) {
        try {
            var response = client.buscarPorId(id);
            return Optional.of(mapper.toDomain(response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
