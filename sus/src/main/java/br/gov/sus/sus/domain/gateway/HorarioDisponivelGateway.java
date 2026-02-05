package br.gov.sus.sus.domain.gateway;

import br.gov.sus.sus.domain.entity.HorarioDisponivel;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

/**
 * Gateway para acesso a dados de Horários Disponíveis.
 * Dados obtidos do json-server (mock externo).
 */
public interface HorarioDisponivelGateway {
    
    Optional<HorarioDisponivel> buscarPorId(Long id);
    
    List<HorarioDisponivel> buscarPorProfissionalId(Long profissionalId);
    
    List<HorarioDisponivel> buscarPorProfissionalIdEDiaSemana(
            Long profissionalId, DayOfWeek diaSemana);
    
    List<HorarioDisponivel> listarTodos();
    
    List<HorarioDisponivel> listarAtivos();
}
