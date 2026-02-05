package br.gov.sus.sus.domain.entity;

public class UnidadeSaude {
    
    private String codigoCnes;
    private String nome;
    private String endereco;
    private String bairro;
    private String cep;
    private Integer codigoMunicipio;
    private Integer codigoUf;
    private String telefone;
    private Double latitude;
    private Double longitude;
    private String tipoUnidade;
    
    public UnidadeSaude() {
    }
    
    public UnidadeSaude(String codigoCnes, String nome, String endereco, String bairro, 
                        String cep, Integer codigoMunicipio, Integer codigoUf, 
                        String telefone, Double latitude, Double longitude, String tipoUnidade) {
        this.codigoCnes = codigoCnes;
        this.nome = nome;
        this.endereco = endereco;
        this.bairro = bairro;
        this.cep = cep;
        this.codigoMunicipio = codigoMunicipio;
        this.codigoUf = codigoUf;
        this.telefone = telefone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.tipoUnidade = tipoUnidade;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String codigoCnes;
        private String nome;
        private String endereco;
        private String bairro;
        private String cep;
        private Integer codigoMunicipio;
        private Integer codigoUf;
        private String telefone;
        private Double latitude;
        private Double longitude;
        private String tipoUnidade;
        
        public Builder codigoCnes(String codigoCnes) {
            this.codigoCnes = codigoCnes;
            return this;
        }
        
        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }
        
        public Builder endereco(String endereco) {
            this.endereco = endereco;
            return this;
        }
        
        public Builder bairro(String bairro) {
            this.bairro = bairro;
            return this;
        }
        
        public Builder cep(String cep) {
            this.cep = cep;
            return this;
        }
        
        public Builder codigoMunicipio(Integer codigoMunicipio) {
            this.codigoMunicipio = codigoMunicipio;
            return this;
        }
        
        public Builder codigoUf(Integer codigoUf) {
            this.codigoUf = codigoUf;
            return this;
        }
        
        public Builder telefone(String telefone) {
            this.telefone = telefone;
            return this;
        }
        
        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }
        
        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }
        
        public Builder tipoUnidade(String tipoUnidade) {
            this.tipoUnidade = tipoUnidade;
            return this;
        }
        
        public UnidadeSaude build() {
            return new UnidadeSaude(codigoCnes, nome, endereco, bairro, cep, 
                    codigoMunicipio, codigoUf, telefone, latitude, longitude, tipoUnidade);
        }
    }
    
    // Getters e Setters
    public String getCodigoCnes() {
        return codigoCnes;
    }
    
    public void setCodigoCnes(String codigoCnes) {
        this.codigoCnes = codigoCnes;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public String getBairro() {
        return bairro;
    }
    
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    
    public String getCep() {
        return cep;
    }
    
    public void setCep(String cep) {
        this.cep = cep;
    }
    
    public Integer getCodigoMunicipio() {
        return codigoMunicipio;
    }
    
    public void setCodigoMunicipio(Integer codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }
    
    public Integer getCodigoUf() {
        return codigoUf;
    }
    
    public void setCodigoUf(Integer codigoUf) {
        this.codigoUf = codigoUf;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getTipoUnidade() {
        return tipoUnidade;
    }
    
    public void setTipoUnidade(String tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }
}
