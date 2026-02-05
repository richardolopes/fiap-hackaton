package br.gov.sus.sus.infrastructure.client.dto;

public class HorarioApiResponse {
    
    private Long id;
    private Long profissionalId;
    private String diaSemana;
    private String horaInicio;
    private String horaFim;
    private Boolean ativo;
    
    public HorarioApiResponse() {
    }
    
    public HorarioApiResponse(Long id, Long profissionalId, String diaSemana,
                              String horaInicio, String horaFim, Boolean ativo) {
        this.id = id;
        this.profissionalId = profissionalId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.ativo = ativo;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProfissionalId() {
        return profissionalId;
    }
    
    public void setProfissionalId(Long profissionalId) {
        this.profissionalId = profissionalId;
    }
    
    public String getDiaSemana() {
        return diaSemana;
    }
    
    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }
    
    public String getHoraInicio() {
        return horaInicio;
    }
    
    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }
    
    public String getHoraFim() {
        return horaFim;
    }
    
    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
