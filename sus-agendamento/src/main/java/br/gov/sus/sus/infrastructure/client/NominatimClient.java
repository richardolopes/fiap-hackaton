package br.gov.sus.sus.infrastructure.client;

import br.gov.sus.sus.infrastructure.client.dto.NominatimResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Client para API do Nominatim (OpenStreetMap) - Geocoding
 * Usado para obter coordenadas geográficas a partir do CEP
 */
@FeignClient(
        name = "nominatimClient",
        url = "https://nominatim.openstreetmap.org"
)
public interface NominatimClient {

    @GetMapping("/search")
    List<NominatimResponse> buscarPorCep(
            @RequestParam("postalcode") String cep,
            @RequestParam("country") String country,
            @RequestParam("format") String format,
            @RequestParam("limit") int limit,
            @RequestHeader("User-Agent") String userAgent
    );

    @GetMapping("/search")
    List<NominatimResponse> buscarPorEndereco(
            @RequestParam("q") String endereco,
            @RequestParam("format") String format,
            @RequestParam("limit") int limit,
            @RequestHeader("User-Agent") String userAgent
    );
}
