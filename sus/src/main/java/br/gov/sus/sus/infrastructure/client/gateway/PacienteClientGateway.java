package br.gov.sus.sus.infrastructure.client.gateway;

import br.gov.sus.sus.domain.entity.Paciente;
import br.gov.sus.sus.domain.gateway.PacienteGateway;
import br.gov.sus.sus.infrastructure.client.PacienteClient;
import br.gov.sus.sus.infrastructure.persistence.mapper.PacienteMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PacienteClientGateway implements PacienteGateway {
    
    private final PacienteClient client;
    private final PacienteMapper mapper;
    
    public PacienteClientGateway(PacienteClient client, PacienteMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }
    
    @Override
    public Paciente salvar(Paciente paciente) {
        var response = client.criar(mapper.toApiResponse(paciente));
        return mapper.toDomain(response);
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
    
    @Override
    public Optional<Paciente> buscarPorCpf(String cpf) {
        try {
            return client.buscarPorCpf(cpf).stream()
                    .map(mapper::toDomain)
                    .findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Paciente> buscarPorCartaoSus(String cartaoSus) {
        try {
            return client.buscarPorCartaoSus(cartaoSus).stream()
                    .map(mapper::toDomain)
                    .findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    @Override
    public List<Paciente> listarTodos() {
        try {
            return client.buscarTodos().stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }
    
    @Override
    public boolean existePorCpf(String cpf) {
        return buscarPorCpf(cpf).isPresent();
    }
    
    @Override
    public boolean existePorCartaoSus(String cartaoSus) {
        return buscarPorCartaoSus(cartaoSus).isPresent();
    }
    
    @Override
    public void deletar(Long id) {
        try {
            client.deletar(id);
        } catch (Exception e) {
            // ignore
        }
    }
}
