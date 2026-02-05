package br.gov.sus.sus.infrastructure.client.dto;

public class ProfissionalApiResponse {
    
    private Long id;
    private String nomeCompleto;
    private String registroConselho;
    private Long especialidadeId;
    private String codigoCnesUnidade;
    private Boolean ativo;
    
    public ProfissionalApiResponse() {
    }
    
    public ProfissionalApiResponse(Long id, String nomeCompleto, String registroConselho,
                                   Long especialidadeId, String codigoCnesUnidade, Boolean ativo) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.registroConselho = registroConselho;
        this.especialidadeId = especialidadeId;
        this.codigoCnesUnidade = codigoCnesUnidade;
        this.ativo = ativo;
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
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
