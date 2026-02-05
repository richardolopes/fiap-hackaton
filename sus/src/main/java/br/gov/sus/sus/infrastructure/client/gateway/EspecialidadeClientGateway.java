package br.gov.sus.sus.infrastructure.client.gateway;

import br.gov.sus.sus.domain.entity.Especialidade;
import br.gov.sus.sus.domain.gateway.EspecialidadeGateway;
import br.gov.sus.sus.infrastructure.client.EspecialidadeClient;
import br.gov.sus.sus.infrastructure.persistence.mapper.EspecialidadeMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    @Override
    public List<Especialidade> listarAtivas() {
        try {
            return client.buscarTodas().stream()
                    .filter(e -> e.getAtivo() == null || e.getAtivo())
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
    
    @Override
    public List<Especialidade> listarTodas() {
        try {
            return client.buscarTodas().stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
}
