package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.EstabelecimentoSusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Client para API de Estabelecimentos do CNES - Dados Abertos SUS
 * Endpoint: /cnes/estabelecimentos
 * 
 * Códigos de tipo de unidade importantes:
 * - 2 = Posto de Saúde / Centro de Saúde / Unidade Básica
 * - 5 = Hospital Geral
 * - 7 = Hospital Especializado
 * - 22 = Consultório
 * 
 * NOTA: A API limita a 20 resultados por página, por isso usamos paginação com offset.
 */
@FeignClient(
    name = "estabelecimentoSusClient",
    url = "${sus.api.base-url}"
)
public interface EstabelecimentoSusClient {

    @GetMapping("/cnes/estabelecimentos")
    EstabelecimentoSusResponse buscarPorMunicipioComOffset(
        @RequestParam("codigo_municipio") Integer codigoMunicipio,
        @RequestParam("codigo_tipo_unidade") Integer codigoTipoUnidade,
        @RequestParam("limit") int limit,
        @RequestParam("offset") int offset
    );
}
