package br.gov.sus.agendamento.infrastructure.client.gateway;

import br.gov.sus.shared.domain.entity.UnidadeSaude;
import br.gov.sus.agendamento.domain.gateway.UnidadeSaudeGateway;
import br.gov.sus.agendamento.infrastructure.client.EstabelecimentoSusClient;
import br.gov.sus.agendamento.infrastructure.client.NominatimClient;
import br.gov.sus.agendamento.infrastructure.client.ViaCepClient;
import br.gov.sus.agendamento.infrastructure.client.dto.EstabelecimentoSusResponse;
import br.gov.sus.agendamento.infrastructure.client.dto.NominatimResponse;
import br.gov.sus.agendamento.infrastructure.client.dto.ViaCepResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gateway para Unidades de Saúde usando APIs reais do SUS:
 * - /cnes/estabelecimentos - Lista estabelecimentos de saúde (API que funciona corretamente com filtros)
 * - ViaCEP - Para obter código IBGE do município a partir do CEP
 * - Nominatim (OpenStreetMap) - Para geocoding (obter coordenadas do CEP)
 * <p>
 * NOTA: A API do SUS tem problemas nos filtros de busca por codigo_cnes e codigo_cep.
 * A única busca confiável é por codigo_municipio.
 * Por isso, usamos um cache em memória para guardar as UBS encontradas.
 */
@Slf4j
@Component
public class UnidadeSaudeClientGateway implements UnidadeSaudeGateway {

    private static final int CODIGO_TIPO_UNIDADE_UBS = 2; // Posto de Saúde / Centro de Saúde / Unidade Básica
    private static final String USER_AGENT = "SUS-Agendamento-App/1.0";

    private final EstabelecimentoSusClient estabelecimentoClient;
    private final ViaCepClient viaCepClient;
    private final NominatimClient nominatimClient;

    private final Map<String, UnidadeSaude> cacheUnidades = new ConcurrentHashMap<>();

    public UnidadeSaudeClientGateway(EstabelecimentoSusClient estabelecimentoClient,
                                     ViaCepClient viaCepClient,
                                     NominatimClient nominatimClient) {
        this.estabelecimentoClient = estabelecimentoClient;
        this.viaCepClient = viaCepClient;
        this.nominatimClient = nominatimClient;
    }

    @Override
    public Optional<UnidadeSaude> buscarPorCodigoCnes(String codigoCnes) {
        if (cacheUnidades.containsKey(codigoCnes)) {
            log.info("UBS encontrada no cache: {}", codigoCnes);
            return Optional.of(cacheUnidades.get(codigoCnes));
        }

        log.warn("UBS não encontrada no cache para código CNES: {}. " +
                "A API do SUS não suporta busca confiável por CNES.", codigoCnes);

        return Optional.of(UnidadeSaude.builder()
                .codigoCnes(codigoCnes)
                .nome("UBS - Código CNES: " + codigoCnes)
                .tipoUnidade("UBS")
                .build());
    }

    /**
     * Busca a UBS mais próxima do CEP informado usando a API de estabelecimentos CNES.
     * <p>
     * A lógica é:
     * 1. Obter o código IBGE do município pelo CEP (via ViaCEP)
     * 2. Obter coordenadas geográficas do CEP (via Nominatim)
     * 3. Buscar todas as UBS do município pela API CNES com paginação
     * 4. Calcular a distância entre o CEP e cada UBS usando coordenadas
     * 5. Retornar a UBS mais próxima e cachear o resultado
     */
    @Override
    public Optional<UnidadeSaude> buscarUbsMaisProximaPorCep(String cep) {
        try {
            log.info("Buscando UBS mais próxima do CEP: {}", cep);

            String cepLimpo = cep.replaceAll("[^0-9]", "");
            ViaCepResponse viaCepResponse = viaCepClient.buscarPorCep(cepLimpo);

            if (viaCepResponse == null || viaCepResponse.getErro() != null && viaCepResponse.getErro()) {
                log.warn("CEP não encontrado: {}", cep);
                return Optional.empty();
            }

            String codigoIbge = viaCepResponse.getIbge();
            Integer codigoMunicipio = Integer.parseInt(codigoIbge.substring(0, 6)); // Remove dígito verificador
            log.info("CEP {} pertence ao município IBGE: {} ({})", cep, codigoMunicipio, viaCepResponse.getLocalidade());

            Double[] coordenadasCep = obterCoordenadasAproximadas(cepLimpo, viaCepResponse);
            log.info("Coordenadas do CEP: lat={}, lon={}", coordenadasCep[0], coordenadasCep[1]);

            List<EstabelecimentoSusResponse.Estabelecimento> todasUbs = buscarTodasUbsDoMunicipio(codigoMunicipio);

            if (todasUbs.isEmpty()) {
                log.warn("Nenhuma UBS encontrada no município: {} ({})", codigoMunicipio, viaCepResponse.getLocalidade());
                return Optional.empty();
            }

            log.info("Total de {} UBS encontradas no município {}", todasUbs.size(), viaCepResponse.getLocalidade());

            EstabelecimentoSusResponse.Estabelecimento ubsMaisProxima = encontrarUbsMaisProxima(
                    todasUbs,
                    coordenadasCep[0],
                    coordenadasCep[1]
            );

            if (ubsMaisProxima == null) {
                ubsMaisProxima = todasUbs.get(0);
            }

            log.info("UBS selecionada: {} - {} (Bairro: {})",
                    ubsMaisProxima.getCodigoCnes(),
                    ubsMaisProxima.getNomeFantasia(),
                    ubsMaisProxima.getBairroEstabelecimento());

            UnidadeSaude unidadeSaude = estabelecimentoToDomain(ubsMaisProxima);
            cacheUnidades.put(unidadeSaude.getCodigoCnes(), unidadeSaude);
            log.info("UBS adicionada ao cache: {}", unidadeSaude.getCodigoCnes());

            return Optional.of(unidadeSaude);

        } catch (Exception e) {
            log.error("Erro ao buscar UBS mais próxima do CEP {}: {}", cep, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Busca todas as UBS de um município usando paginação.
     * A API do SUS limita a 20 resultados por página.
     */
    private List<EstabelecimentoSusResponse.Estabelecimento> buscarTodasUbsDoMunicipio(Integer codigoMunicipio) {
        List<EstabelecimentoSusResponse.Estabelecimento> todasUbs = new ArrayList<>();
        int offset = 0;
        int limit = 20;
        int maxPaginas = 50;
        int paginaAtual = 0;

        while (paginaAtual < maxPaginas) {
            try {
                EstabelecimentoSusResponse response = estabelecimentoClient.buscarPorMunicipioComOffset(
                        codigoMunicipio,
                        CODIGO_TIPO_UNIDADE_UBS,
                        limit,
                        offset
                );

                if (response == null || response.getEstabelecimentos() == null || response.getEstabelecimentos().isEmpty()) {
                    break;
                }

                todasUbs.addAll(response.getEstabelecimentos());

                if (response.getEstabelecimentos().size() < limit) {
                    break;
                }

                offset += limit;
                paginaAtual++;

            } catch (Exception e) {
                log.warn("Erro ao buscar página {} de UBS: {}", paginaAtual, e.getMessage());
                break;
            }
        }

        log.info("Busca paginada concluída: {} UBS em {} páginas", todasUbs.size(), paginaAtual + 1);
        return todasUbs;
    }

    private EstabelecimentoSusResponse.Estabelecimento encontrarUbsMaisProxima(
            List<EstabelecimentoSusResponse.Estabelecimento> ubsList,
            Double latCep,
            Double lonCep) {

        EstabelecimentoSusResponse.Estabelecimento maisProxima = null;
        double menorDistancia = Double.MAX_VALUE;

        for (EstabelecimentoSusResponse.Estabelecimento ubs : ubsList) {
            Double latUbs = ubs.getLatitude();
            Double lonUbs = ubs.getLongitude();

            double distancia = calcularDistancia(latCep, lonCep, latUbs, lonUbs);

            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                maisProxima = ubs;
            }
        }

        if (maisProxima != null) {
            log.info("UBS mais próxima encontrada a {} km: {}",
                    String.format("%.2f", menorDistancia), maisProxima.getNomeFantasia());
        }

        return maisProxima;
    }

    /**
     * Calcula a distância em km entre dois pontos usando a fórmula de Haversine
     */
    private double calcularDistancia(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }

        final double R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Obtém coordenadas geográficas do CEP usando Nominatim (OpenStreetMap).
     * Se não conseguir, usa coordenadas aproximadas baseadas na UF.
     */
    private Double[] obterCoordenadasAproximadas(String cep, ViaCepResponse viaCep) {
        try {
            String cepFormatado = cep.substring(0, 5) + "-" + cep.substring(5);
            List<NominatimResponse> nominatimResults = nominatimClient.buscarPorCep(
                    cepFormatado, "Brazil", "json", 1, USER_AGENT);

            if (nominatimResults != null && !nominatimResults.isEmpty()) {
                NominatimResponse resultado = nominatimResults.get(0);
                Double lat = resultado.getLatitude();
                Double lon = resultado.getLongitude();

                if (lat != null && lon != null) {
                    log.info("Coordenadas obtidas via Nominatim para CEP {}: lat={}, lon={}",
                            cep, lat, lon);
                    return new Double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao obter coordenadas via Nominatim para CEP {}: {}", cep, e.getMessage());
        }

        try {
            if (viaCep.getLogradouro() != null && viaCep.getLocalidade() != null) {
                String endereco = String.format("%s, %s, %s, Brazil",
                        viaCep.getLogradouro(), viaCep.getLocalidade(), viaCep.getUf());

                List<NominatimResponse> nominatimResults = nominatimClient.buscarPorEndereco(
                        endereco, "json", 1, USER_AGENT);

                if (nominatimResults != null && !nominatimResults.isEmpty()) {
                    NominatimResponse resultado = nominatimResults.get(0);
                    Double lat = resultado.getLatitude();
                    Double lon = resultado.getLongitude();

                    if (lat != null && lon != null) {
                        log.info("Coordenadas obtidas via Nominatim para endereço {}: lat={}, lon={}",
                                endereco, lat, lon);
                        return new Double[]{lat, lon};
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao obter coordenadas via Nominatim para endereço: {}", e.getMessage());
        }

        log.warn("Usando coordenadas padrão para UF: {}", viaCep.getUf());
        Map<String, Double[]> coordenadasPadraoPorUf = new HashMap<>();
        coordenadasPadraoPorUf.put("SP", new Double[]{-23.5505, -46.6333}); // São Paulo
        coordenadasPadraoPorUf.put("RJ", new Double[]{-22.9068, -43.1729}); // Rio de Janeiro
        coordenadasPadraoPorUf.put("MG", new Double[]{-19.9191, -43.9386}); // Belo Horizonte
        coordenadasPadraoPorUf.put("BA", new Double[]{-12.9714, -38.5014}); // Salvador
        coordenadasPadraoPorUf.put("RS", new Double[]{-30.0346, -51.2177}); // Porto Alegre
        coordenadasPadraoPorUf.put("PR", new Double[]{-25.4284, -49.2733}); // Curitiba
        coordenadasPadraoPorUf.put("PE", new Double[]{-8.0476, -34.8770});  // Recife
        coordenadasPadraoPorUf.put("CE", new Double[]{-3.7172, -38.5433});  // Fortaleza
        coordenadasPadraoPorUf.put("DF", new Double[]{-15.7942, -47.8822}); // Brasília

        String uf = viaCep.getUf();
        if (uf != null && coordenadasPadraoPorUf.containsKey(uf)) {
            return coordenadasPadraoPorUf.get(uf);
        }

        return new Double[]{-15.7942, -47.8822};
    }

    private UnidadeSaude estabelecimentoToDomain(EstabelecimentoSusResponse.Estabelecimento estab) {
        String nome = estab.getNomeFantasia() != null && !estab.getNomeFantasia().isBlank()
                ? estab.getNomeFantasia()
                : estab.getNomeRazaoSocial();

        String endereco = estab.getEnderecoEstabelecimento();
        if (estab.getNumeroEstabelecimento() != null && !estab.getNumeroEstabelecimento().isBlank()) {
            endereco += ", " + estab.getNumeroEstabelecimento();
        }

        return UnidadeSaude.builder()
                .codigoCnes(String.valueOf(estab.getCodigoCnes()))
                .nome(nome)
                .endereco(endereco)
                .bairro(estab.getBairroEstabelecimento())
                .cep(estab.getCodigoCepEstabelecimento())
                .codigoMunicipio(estab.getCodigoMunicipio())
                .codigoUf(estab.getCodigoUf())
                .telefone(estab.getTelefone())
                .latitude(estab.getLatitude())
                .longitude(estab.getLongitude())
                .tipoUnidade("UBS")
                .build();
    }
}
