package br.gov.sus.sus.infrastructure.config;

import br.gov.sus.sus.domain.gateway.*;
import br.gov.sus.sus.domain.usecase.agendamento.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    
    @Bean
    public CriarAgendamentoPorCepUseCase criarAgendamentoPorCepUseCase(
            AgendamentoGateway agendamentoGateway,
            PacienteGateway pacienteGateway,
            ProfissionalGateway profissionalGateway,
            UnidadeSaudeGateway unidadeSaudeGateway,
            EspecialidadeGateway especialidadeGateway) {
        return new CriarAgendamentoPorCepUseCase(
                agendamentoGateway,
                pacienteGateway,
                profissionalGateway,
                unidadeSaudeGateway,
                especialidadeGateway);
    }
    
    @Bean
    public BuscarAgendamentoPorIdUseCase buscarAgendamentoPorIdUseCase(AgendamentoGateway agendamentoGateway) {
        return new BuscarAgendamentoPorIdUseCase(agendamentoGateway);
    }
    
    @Bean
    public CancelarAgendamentoUseCase cancelarAgendamentoUseCase(AgendamentoGateway agendamentoGateway) {
        return new CancelarAgendamentoUseCase(agendamentoGateway);
    }
}
