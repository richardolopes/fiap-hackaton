package br.gov.sus.sus.domain.entity;

public class Especialidade {

    private Long id;
    private String nome;
    private String descricao;
    private Integer tempoConsultaMinutos;
    private Boolean ativo;

    public Especialidade() {
    }

    public Especialidade(Long id, String nome, String descricao,
                         Integer tempoConsultaMinutos, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tempoConsultaMinutos = tempoConsultaMinutos;
        this.ativo = ativo;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getTempoConsultaMinutos() {
        return tempoConsultaMinutos;
    }

    public void setTempoConsultaMinutos(Integer tempoConsultaMinutos) {
        this.tempoConsultaMinutos = tempoConsultaMinutos;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public static class Builder {
        private Long id;
        private String nome;
        private String descricao;
        private Integer tempoConsultaMinutos;
        private Boolean ativo;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder descricao(String descricao) {
            this.descricao = descricao;
            return this;
        }

        public Builder tempoConsultaMinutos(Integer tempoConsultaMinutos) {
            this.tempoConsultaMinutos = tempoConsultaMinutos;
            return this;
        }

        public Builder ativo(Boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public Especialidade build() {
            return new Especialidade(id, nome, descricao, tempoConsultaMinutos, ativo);
        }
    }
}
