package br.gov.sus.sus.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response da API /cnes/estabelecimentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstabelecimentoSusResponse {

    @JsonProperty("estabelecimentos")
    private List<Estabelecimento> estabelecimentos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Estabelecimento {

        @JsonProperty("codigo_cnes")
        private Integer codigoCnes;

        @JsonProperty("nome_fantasia")
        private String nomeFantasia;

        @JsonProperty("nome_razao_social")
        private String nomeRazaoSocial;

        @JsonProperty("endereco_estabelecimento")
        private String enderecoEstabelecimento;

        @JsonProperty("numero_estabelecimento")
        private String numeroEstabelecimento;

        @JsonProperty("bairro_estabelecimento")
        private String bairroEstabelecimento;

        @JsonProperty("codigo_cep_estabelecimento")
        private String codigoCepEstabelecimento;

        @JsonProperty("codigo_municipio")
        private Integer codigoMunicipio;

        @JsonProperty("codigo_uf")
        private Integer codigoUf;

        @JsonProperty("numero_telefone_estabelecimento")
        private String telefone;

        @JsonProperty("latitude_estabelecimento_decimo_grau")
        private Double latitude;

        @JsonProperty("longitude_estabelecimento_decimo_grau")
        private Double longitude;

        @JsonProperty("codigo_tipo_unidade")
        private Integer codigoTipoUnidade;

        @JsonProperty("descricao_natureza_juridica_estabelecimento")
        private String descricaoNaturezaJuridica;

        @JsonProperty("estabelecimento_faz_atendimento_ambulatorial_sus")
        private String fazAtendimentoSus;
    }
}
