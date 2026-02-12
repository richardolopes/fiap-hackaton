package br.gov.sus.agendamento.infrastructure.client.gateway;

import br.gov.sus.agendamento.domain.gateway.UnidadeSaudeGateway;
import br.gov.sus.shared.domain.entity.UnidadeSaude;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementação mock do UnidadeSaudeGateway para uso quando a API
 * https://apidadosabertos.saude.gov.br estiver fora do ar.
 * <p>
 * Ativado com: sus.api.mock-enabled=true (no application.yml ou variável de ambiente)
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "sus.api.mock-enabled", havingValue = "true", matchIfMissing = false)
public class UnidadeSaudeMockGateway implements UnidadeSaudeGateway {

    private static final List<UnidadeSaude> UNIDADES_MOCK = List.of(
            UnidadeSaude.builder()
                    .codigoCnes("2025507")
                    .nome("UBS Vila Mariana")
                    .endereco("Rua Norman de Almeida Horta, 77")
                    .bairro("Vila Mariana")
                    .cep("04017030")
                    .codigoMunicipio(355030)
                    .codigoUf(35)
                    .telefone("(11) 5549-1200")
                    .latitude(-23.5870)
                    .longitude(-46.6388)
                    .tipoUnidade("UBS")
                    .build(),
            UnidadeSaude.builder()
                    .codigoCnes("2078015")
                    .nome("UBS Jardim Paulista")
                    .endereco("Rua Leoncio de Carvalho, 316")
                    .bairro("Paraiso")
                    .cep("04003010")
                    .codigoMunicipio(355030)
                    .codigoUf(35)
                    .telefone("(11) 3885-0752")
                    .latitude(-23.5650)
                    .longitude(-46.6500)
                    .tipoUnidade("UBS")
                    .build(),
            UnidadeSaude.builder()
                    .codigoCnes("2077590")
                    .nome("UBS Pinheiros")
                    .endereco("Rua Ferreira de Araujo, 789")
                    .bairro("Pinheiros")
                    .cep("05428001")
                    .codigoMunicipio(355030)
                    .codigoUf(35)
                    .telefone("(11) 3031-2727")
                    .latitude(-23.5670)
                    .longitude(-46.6920)
                    .tipoUnidade("UBS")
                    .build(),
            UnidadeSaude.builder()
                    .codigoCnes("6183085")
                    .nome("UBS Republica")
                    .endereco("Rua do Arouche, 24")
                    .bairro("Republica")
                    .cep("01219010")
                    .codigoMunicipio(355030)
                    .codigoUf(35)
                    .telefone("(11) 3222-0808")
                    .latitude(-23.5430)
                    .longitude(-46.6460)
                    .tipoUnidade("UBS")
                    .build(),
            UnidadeSaude.builder()
                    .codigoCnes("2789329")
                    .nome("UBS Copacabana")
                    .endereco("Rua Siqueira Campos, 93")
                    .bairro("Copacabana")
                    .cep("22031071")
                    .codigoMunicipio(330455)
                    .codigoUf(33)
                    .telefone("(21) 2547-1040")
                    .latitude(-22.9660)
                    .longitude(-43.1870)
                    .tipoUnidade("UBS")
                    .build(),
            UnidadeSaude.builder()
                    .codigoCnes("2708353")
                    .nome("UBS Savassi")
                    .endereco("Rua Rio Grande do Sul, 1200")
                    .bairro("Savassi")
                    .cep("30130131")
                    .codigoMunicipio(310620)
                    .codigoUf(31)
                    .telefone("(31) 3277-5000")
                    .latitude(-19.9340)
                    .longitude(-43.9380)
                    .tipoUnidade("UBS")
                    .build(),
            UnidadeSaude.builder()
                    .codigoCnes("3140155")
                    .nome("UBS Batel")
                    .endereco("Rua Gutenberg, 300")
                    .bairro("Batel")
                    .cep("80420030")
                    .codigoMunicipio(410690)
                    .codigoUf(41)
                    .telefone("(41) 3322-1100")
                    .latitude(-25.4400)
                    .longitude(-49.2850)
                    .tipoUnidade("UBS")
                    .build()
    );

    @Override
    public Optional<UnidadeSaude> buscarPorCodigoCnes(String codigoCnes) {
        log.info("[MOCK] Buscando UBS por código CNES: {}", codigoCnes);

        return UNIDADES_MOCK.stream()
                .filter(u -> u.getCodigoCnes().equals(codigoCnes))
                .findFirst()
                .or(() -> {
                    log.info("[MOCK] CNES {} não encontrado nos dados mock, retornando UBS genérica", codigoCnes);
                    return Optional.of(UnidadeSaude.builder()
                            .codigoCnes(codigoCnes)
                            .nome("UBS Mock - CNES " + codigoCnes)
                            .endereco("Rua Exemplo, 100")
                            .bairro("Centro")
                            .cep("01001000")
                            .codigoMunicipio(355030)
                            .codigoUf(35)
                            .telefone("(11) 0000-0000")
                            .latitude(-23.5505)
                            .longitude(-46.6333)
                            .tipoUnidade("UBS")
                            .build());
                });
    }

    @Override
    public Optional<UnidadeSaude> buscarUbsMaisProximaPorCep(String cep) {
        log.info("[MOCK] Buscando UBS mais próxima do CEP: {}", cep);

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        // Determina a região pelo prefixo do CEP e retorna UBS correspondente
        UnidadeSaude ubsSelecionada;
        if (cepLimpo.startsWith("0") || cepLimpo.startsWith("1")) {
            // São Paulo
            ubsSelecionada = UNIDADES_MOCK.get(0); // UBS Vila Mariana
        } else if (cepLimpo.startsWith("2")) {
            // Rio de Janeiro
            ubsSelecionada = UNIDADES_MOCK.get(4); // UBS Copacabana
        } else if (cepLimpo.startsWith("3")) {
            // Minas Gerais
            ubsSelecionada = UNIDADES_MOCK.get(5); // UBS Savassi
        } else if (cepLimpo.startsWith("8")) {
            // Paraná
            ubsSelecionada = UNIDADES_MOCK.get(6); // UBS Batel
        } else {
            // Default: São Paulo
            ubsSelecionada = UNIDADES_MOCK.get(0);
        }

        log.info("[MOCK] UBS selecionada para CEP {}: {} - {}",
                cep, ubsSelecionada.getCodigoCnes(), ubsSelecionada.getNome());

        return Optional.of(ubsSelecionada);
    }
}
