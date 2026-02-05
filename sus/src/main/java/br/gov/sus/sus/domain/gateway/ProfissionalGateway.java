package br.gov.sus.sus.domain.gateway;

import br.gov.sus.sus.domain.entity.Profissional;

import java.util.List;
import java.util.Optional;

/**
 * Gateway para acesso a dados de Profissionais.
 * Dados obtidos do json-server (mock externo).
 */
public interface ProfissionalGateway {
    
    Optional<Profissional> buscarPorId(Long id);
    
    List<Profissional> buscarPorCodigoCnesUnidade(String codigoCnes);
    
    List<Profissional> buscarPorEspecialidadeId(Long especialidadeId);
    
    List<Profissional> buscarPorCodigoCnesUnidadeEEspecialidadeId(
            String codigoCnes, Long especialidadeId);
    
    List<Profissional> listarTodos();
    
    List<Profissional> listarAtivos();
}
