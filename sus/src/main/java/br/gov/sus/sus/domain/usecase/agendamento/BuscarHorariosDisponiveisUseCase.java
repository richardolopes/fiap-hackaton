package br.gov.sus.sus.domain.usecase.agendamento;

import br.gov.sus.sus.domain.entity.Profissional;
import br.gov.sus.sus.domain.gateway.ProfissionalGateway;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BuscarHorariosDisponiveisUseCase {
    
    private final ProfissionalGateway profissionalGateway;
    
    public BuscarHorariosDisponiveisUseCase(ProfissionalGateway profissionalGateway) {
        this.profissionalGateway = profissionalGateway;
    }
    
    public List<HorarioDisponivelOutput> executar(String codigoCnesUnidade, Long especialidadeId, LocalDate data) {
        List<Profissional> profissionais = profissionalGateway
                .buscarPorCodigoCnesUnidadeEEspecialidadeId(codigoCnesUnidade, especialidadeId);
        
        List<HorarioDisponivelOutput> horariosDisponiveis = new ArrayList<>();
        
        for (Profissional profissional : profissionais) {
            LocalTime hora = LocalTime.of(8, 0);
            LocalTime horaFim = LocalTime.of(17, 0);
            
            while (hora.isBefore(horaFim)) {
                LocalDateTime dataHora = LocalDateTime.of(data, hora);
                
                if (dataHora.isAfter(LocalDateTime.now())) {
                    horariosDisponiveis.add(new HorarioDisponivelOutput(
                            dataHora,
                            profissional.getId(),
                            profissional.getNomeCompleto(),
                            codigoCnesUnidade,
                            codigoCnesUnidade,
                            especialidadeId,
                            "Especialidade",
                            true
                    ));
                }
                
                hora = hora.plusMinutes(30);
            }
        }
        
        return horariosDisponiveis;
    }
    
    public static class HorarioDisponivelOutput {
        private final LocalDateTime dataHora;
        private final Long profissionalId;
        private final String nomeProfissional;
        private final String codigoCnesUnidade;
        private final String nomeUnidade;
        private final Long especialidadeId;
        private final String nomeEspecialidade;
        private final boolean disponivel;
        
        public HorarioDisponivelOutput(LocalDateTime dataHora, Long profissionalId, String nomeProfissional,
                                       String codigoCnesUnidade, String nomeUnidade, Long especialidadeId,
                                       String nomeEspecialidade, boolean disponivel) {
            this.dataHora = dataHora;
            this.profissionalId = profissionalId;
            this.nomeProfissional = nomeProfissional;
            this.codigoCnesUnidade = codigoCnesUnidade;
            this.nomeUnidade = nomeUnidade;
            this.especialidadeId = especialidadeId;
            this.nomeEspecialidade = nomeEspecialidade;
            this.disponivel = disponivel;
        }
        
        public LocalDateTime getDataHora() { return dataHora; }
        public Long getProfissionalId() { return profissionalId; }
        public String getNomeProfissional() { return nomeProfissional; }
        public String getCodigoCnesUnidade() { return codigoCnesUnidade; }
        public String getNomeUnidade() { return nomeUnidade; }
        public Long getEspecialidadeId() { return especialidadeId; }
        public String getNomeEspecialidade() { return nomeEspecialidade; }
        public boolean isDisponivel() { return disponivel; }
    }
}
