package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.historial_medico.CreateHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.DeleteHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.EditHistorialMedicoService;
import es.refugio.refugio.application.service.historial_medico.FindHistorialMedicoService;
import es.refugio.refugio.application.usecase.historial_medico.CreateHistorialMedicoUseCase;
import es.refugio.refugio.application.usecase.historial_medico.DeleteHistorialMedicoUseCase;
import es.refugio.refugio.application.usecase.historial_medico.EditHistorialMedicoUseCase;
import es.refugio.refugio.application.usecase.historial_medico.FindHistorialMedicoUseCase;
import es.refugio.refugio.domain.repository.HistorialMedicoRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.historial_medico.HistorialMedicoEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.historial_medico.HistorialMedicoJpaRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HistorialMedicoConfig {

    private final HistorialMedicoEntityJpaRepository jpaRepository;

    @Bean
    public HistorialMedicoRepository historialMedicoRepository() {
        return new HistorialMedicoJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public CreateHistorialMedicoUseCase createHistorialMedicoUseCase() {
        return new CreateHistorialMedicoUseCase(historialMedicoRepository());
    }

    @Bean
    public CreateHistorialMedicoService createHistorialMedicoService() {
        return new CreateHistorialMedicoService(createHistorialMedicoUseCase());
    }

    @Bean
    public FindHistorialMedicoUseCase findHistorialMedicoUseCase() {
        return new FindHistorialMedicoUseCase(historialMedicoRepository());
    }

    @Bean
    public FindHistorialMedicoService findHistorialMedicoService() {
        return new FindHistorialMedicoService(findHistorialMedicoUseCase());
    }

    @Bean
    public DeleteHistorialMedicoUseCase deleteHistorialMedicoUseCase() {
        return new DeleteHistorialMedicoUseCase(historialMedicoRepository());
    }

    @Bean
    public DeleteHistorialMedicoService deleteHistorialMedicoService() {
        return new DeleteHistorialMedicoService(deleteHistorialMedicoUseCase());
    }

    @Bean
    public EditHistorialMedicoUseCase editHistorialMedicoUseCase() {
        return new EditHistorialMedicoUseCase(historialMedicoRepository());
    }

    @Bean
    public EditHistorialMedicoService editHistorialMedicoService() {
        return new EditHistorialMedicoService(editHistorialMedicoUseCase());
    }
}
