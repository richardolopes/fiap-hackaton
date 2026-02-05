package br.gov.sus.sus.infrastructure.client.gateway;

import br.gov.sus.sus.domain.entity.HorarioDisponivel;
import br.gov.sus.sus.domain.gateway.HorarioDisponivelGateway;
import br.gov.sus.sus.infrastructure.client.HorarioClient;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Component
public class HorarioDisponivelClientGateway implements HorarioDisponivelGateway {
    
    private final HorarioClient client;
    
    public HorarioDisponivelClientGateway(HorarioClient client) {
        this.client = client;
    }
    
    @Override
    public Optional<HorarioDisponivel> buscarPorId(Long id) {
        return Optional.empty();
    }
    
    @Override
    public List<HorarioDisponivel> buscarPorProfissionalId(Long profissionalId) {
        return List.of();
    }
    
    @Override
    public List<HorarioDisponivel> buscarPorProfissionalIdEDiaSemana(Long profissionalId, DayOfWeek diaSemana) {
        return List.of();
    }
    
    @Override
    public List<HorarioDisponivel> listarTodos() {
        return List.of();
    }
    
    @Override
    public List<HorarioDisponivel> listarAtivos() {
        return List.of();
    }
}
