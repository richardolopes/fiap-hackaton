package br.gov.sus.sus.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Paciente {

    private Long id;
    private String nomeCompleto;
    private String cpf;
    private String cartaoSus;
    private LocalDate dataNascimento;
    private String telefone;
    private String email;
    private String endereco;
    private String municipio;
    private String uf;
    private String cep;
    private LocalDateTime dataCadastro;

    public Paciente() {
    }

    public Paciente(Long id, String nomeCompleto, String cpf, String cartaoSus,
                    LocalDate dataNascimento, String telefone, String email,
                    String endereco, String municipio, String uf, String cep,
                    LocalDateTime dataCadastro) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.cartaoSus = cartaoSus;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.municipio = municipio;
        this.uf = uf;
        this.cep = cep;
        this.dataCadastro = dataCadastro;
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

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCartaoSus() {
        return cartaoSus;
    }

    public void setCartaoSus(String cartaoSus) {
        this.cartaoSus = cartaoSus;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public static class Builder {
        private Long id;
        private String nomeCompleto;
        private String cpf;
        private String cartaoSus;
        private LocalDate dataNascimento;
        private String telefone;
        private String email;
        private String endereco;
        private String municipio;
        private String uf;
        private String cep;
        private LocalDateTime dataCadastro;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nomeCompleto(String nomeCompleto) {
            this.nomeCompleto = nomeCompleto;
            return this;
        }

        public Builder cpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder cartaoSus(String cartaoSus) {
            this.cartaoSus = cartaoSus;
            return this;
        }

        public Builder dataNascimento(LocalDate dataNascimento) {
            this.dataNascimento = dataNascimento;
            return this;
        }

        public Builder telefone(String telefone) {
            this.telefone = telefone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder endereco(String endereco) {
            this.endereco = endereco;
            return this;
        }

        public Builder municipio(String municipio) {
            this.municipio = municipio;
            return this;
        }

        public Builder uf(String uf) {
            this.uf = uf;
            return this;
        }

        public Builder cep(String cep) {
            this.cep = cep;
            return this;
        }

        public Builder dataCadastro(LocalDateTime dataCadastro) {
            this.dataCadastro = dataCadastro;
            return this;
        }

        public Paciente build() {
            return new Paciente(id, nomeCompleto, cpf, cartaoSus, dataNascimento,
                    telefone, email, endereco, municipio, uf, cep, dataCadastro);
        }
    }
}
