package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.UbsSusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Client para API de Unidades Básicas de Saúde - Dados Abertos SUS
 * Endpoint: /assistencia-a-saude/unidade-basicas-de-saude
 */
@FeignClient(
    name = "ubsSusClient",
    url = "${sus.api.base-url}"
)
public interface UbsSusClient {

    @GetMapping("/assistencia-a-saude/unidade-basicas-de-saude")
    UbsSusResponse listarUbs(
        @RequestParam("limit") int limit,
        @RequestParam("offset") int offset
    );

    @GetMapping("/assistencia-a-saude/unidade-basicas-de-saude")
    UbsSusResponse buscarPorCnes(
        @RequestParam("cnes") String cnes,
        @RequestParam("limit") int limit
    );

    @GetMapping("/assistencia-a-saude/unidade-basicas-de-saude")
    UbsSusResponse buscarPorIbge(
        @RequestParam("ibge") String codigoIbge,
        @RequestParam("limit") int limit,
        @RequestParam("offset") int offset
    );

    @GetMapping("/assistencia-a-saude/unidade-basicas-de-saude")
    UbsSusResponse buscarPorUf(
        @RequestParam("uf") String codigoUf,
        @RequestParam("limit") int limit,
        @RequestParam("offset") int offset
    );
}
