package br.gov.sus.agendamento.infrastructure.client;

import br.gov.sus.agendamento.infrastructure.client.dto.ViaCepResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client para API ViaCEP - Consulta de CEP
 */
@FeignClient(
        name = "viaCepClient",
        url = "https://viacep.com.br"
)
public interface ViaCepClient {

    @GetMapping("/ws/{cep}/json/")
    ViaCepResponse buscarPorCep(@PathVariable("cep") String cep);
}
