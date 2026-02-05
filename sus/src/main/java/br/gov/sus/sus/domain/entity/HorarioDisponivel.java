package br.gov.sus.sus.domain.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class HorarioDisponivel {
    
    private Long id;
    private Long profissionalId;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private Boolean ativo;
    
    // Campo para enriquecimento
    private Profissional profissional;
    
    public HorarioDisponivel() {
    }
    
    public HorarioDisponivel(Long id, Long profissionalId, DayOfWeek diaSemana,
                             LocalTime horaInicio, LocalTime horaFim, Boolean ativo) {
        this.id = id;
        this.profissionalId = profissionalId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.ativo = ativo;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private Long profissionalId;
        private DayOfWeek diaSemana;
        private LocalTime horaInicio;
        private LocalTime horaFim;
        private Boolean ativo;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder profissionalId(Long profissionalId) {
            this.profissionalId = profissionalId;
            return this;
        }
        
        public Builder diaSemana(DayOfWeek diaSemana) {
            this.diaSemana = diaSemana;
            return this;
        }
        
        public Builder horaInicio(LocalTime horaInicio) {
            this.horaInicio = horaInicio;
            return this;
        }
        
        public Builder horaFim(LocalTime horaFim) {
            this.horaFim = horaFim;
            return this;
        }
        
        public Builder ativo(Boolean ativo) {
            this.ativo = ativo;
            return this;
        }
        
        public HorarioDisponivel build() {
            return new HorarioDisponivel(id, profissionalId, diaSemana, horaInicio, horaFim, ativo);
        }
    }
    
    // Getters e Setters
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
    
    public Profissional getProfissional() {
        return profissional;
    }
    
    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }
    
    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }
    
    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }
    
    public LocalTime getHoraInicio() {
        return horaInicio;
    }
    
    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }
    
    public LocalTime getHoraFim() {
        return horaFim;
    }
    
    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
