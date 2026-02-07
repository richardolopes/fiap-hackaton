package br.gov.sus.sus.domain.gateway;

import br.gov.sus.sus.domain.entity.UnidadeSaude;

import java.util.Optional;

/**
 * Gateway para acesso a dados de Unidades de Saúde.
 * Dados obtidos da API real do DataSUS (CNES).
 */
public interface UnidadeSaudeGateway {

    Optional<UnidadeSaude> buscarPorCodigoCnes(String codigoCnes);

    Optional<UnidadeSaude> buscarUbsMaisProximaPorCep(String cep);
}
