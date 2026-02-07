package br.gov.sus.sus.infrastructure.client.dto;

public class EspecialidadeApiResponse {

    private Long id;
    private String nome;
    private String descricao;
    private Integer tempoConsultaMinutos;
    private Boolean ativo;

    public EspecialidadeApiResponse() {
    }

    public EspecialidadeApiResponse(Long id, String nome, String descricao,
                                    Integer tempoConsultaMinutos, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.tempoConsultaMinutos = tempoConsultaMinutos;
        this.ativo = ativo;
    }

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
}
