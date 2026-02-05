package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.ProfissionalApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "profissionalClient", url = "${api.mock-server.url}")
public interface ProfissionalClient {

    @GetMapping("/profissionais")
    List<ProfissionalApiResponse> buscarTodos();

    @GetMapping("/profissionais/{id}")
    ProfissionalApiResponse buscarPorId(@PathVariable("id") Long id);

    @GetMapping("/profissionais")
    List<ProfissionalApiResponse> buscarPorEspecialidade(@RequestParam("especialidadeId") Long especialidadeId);

    @GetMapping("/profissionais")
    List<ProfissionalApiResponse> buscarPorUnidade(@RequestParam("codigoCnesUnidade") String codigoCnes);

    @GetMapping("/profissionais")
    List<ProfissionalApiResponse> buscarPorEspecialidadeEUnidade(
            @RequestParam("especialidadeId") Long especialidadeId,
            @RequestParam("codigoCnesUnidade") String codigoCnes);

    @GetMapping("/profissionais")
    List<ProfissionalApiResponse> buscarPorAtivo(@RequestParam("ativo") boolean ativo);
}
