package br.gov.sus.sus.domain.entity;

public class Profissional {

    private Long id;
    private String nomeCompleto;
    private String registroConselho;
    private Long especialidadeId;
    private String codigoCnesUnidade;
    private Boolean ativo;

    private Especialidade especialidade;
    private UnidadeSaude unidadeSaude;

    public Profissional() {
    }

    public Profissional(Long id, String nomeCompleto, String registroConselho,
                        Long especialidadeId, String codigoCnesUnidade, Boolean ativo) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.registroConselho = registroConselho;
        this.especialidadeId = especialidadeId;
        this.codigoCnesUnidade = codigoCnesUnidade;
        this.ativo = ativo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getRegistroConselho() {
        return registroConselho;
    }

    public void setRegistroConselho(String registroConselho) {
        this.registroConselho = registroConselho;
    }

    public Long getEspecialidadeId() {
        return especialidadeId;
    }

    public void setEspecialidadeId(Long especialidadeId) {
        this.especialidadeId = especialidadeId;
    }

    public String getCodigoCnesUnidade() {
        return codigoCnesUnidade;
    }

    public void setCodigoCnesUnidade(String codigoCnesUnidade) {
        this.codigoCnesUnidade = codigoCnesUnidade;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public static class Builder {
        private Long id;
        private String nomeCompleto;
        private String registroConselho;
        private Long especialidadeId;
        private String codigoCnesUnidade;
        private Boolean ativo;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nomeCompleto(String nomeCompleto) {
            this.nomeCompleto = nomeCompleto;
            return this;
        }

        public Builder registroConselho(String registroConselho) {
            this.registroConselho = registroConselho;
            return this;
        }

        public Builder especialidadeId(Long especialidadeId) {
            this.especialidadeId = especialidadeId;
            return this;
        }

        public Builder codigoCnesUnidade(String codigoCnesUnidade) {
            this.codigoCnesUnidade = codigoCnesUnidade;
            return this;
        }

        public Builder ativo(Boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public Profissional build() {
            return new Profissional(id, nomeCompleto, registroConselho,
                    especialidadeId, codigoCnesUnidade, ativo);
        }
    }
}
