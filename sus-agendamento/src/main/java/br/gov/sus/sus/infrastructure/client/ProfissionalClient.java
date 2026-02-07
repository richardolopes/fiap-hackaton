package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.ProfissionalApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "profissionalClient", url = "${api.mock-server.url}")
public interface ProfissionalClient {

    @GetMapping("/profissionais/{id}")
    ProfissionalApiResponse buscarPorId(@PathVariable("id") Long id);

    @GetMapping("/profissionais")
    List<ProfissionalApiResponse> buscarPorEspecialidade(@RequestParam("especialidadeId") Long especialidadeId);
}
