package br.gov.sus.sus.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response da API /assistencia-a-saude/unidade-basicas-de-saude
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbsSusResponse {

    @JsonProperty("ubs")
    private List<Ubs> ubs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ubs {
        
        @JsonProperty("cnes")
        private String cnes;

        @JsonProperty("nome")
        private String nome;

        @JsonProperty("logradouro")
        private String logradouro;

        @JsonProperty("bairro")
        private String bairro;

        @JsonProperty("ibge")
        private String ibge;

        @JsonProperty("uf")
        private String uf;

        @JsonProperty("latitude")
        private String latitude;

        @JsonProperty("longitude")
        private String longitude;
    }
}
