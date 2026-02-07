package br.gov.sus.sus.domain.gateway;

import br.gov.sus.sus.domain.entity.Especialidade;

import java.util.Optional;

/**
 * Gateway para acesso a dados de Especialidades.
 * Dados obtidos do json-server (mock externo).
 */
public interface EspecialidadeGateway {
    
    Optional<Especialidade> buscarPorId(Long id);
}
