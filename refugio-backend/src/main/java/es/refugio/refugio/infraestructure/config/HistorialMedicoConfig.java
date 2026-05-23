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
import es.refugio.refugio.infraestructure.mapper.HistorialMedicoMapper;

@Configuration
public class HistorialMedicoConfig {

    @Bean
    public HistorialMedicoRepository historialMedicoRepository(HistorialMedicoEntityJpaRepository jpaRepository, HistorialMedicoMapper historialMedicoMapper) {
        return new HistorialMedicoJpaRepositoryImpl(jpaRepository, historialMedicoMapper);
    }

    @Bean
    public CreateHistorialMedicoUseCase createHistorialMedicoUseCase(HistorialMedicoRepository repository) {
        return new CreateHistorialMedicoUseCase(repository);
    }

    @Bean
    public CreateHistorialMedicoService createHistorialMedicoService(CreateHistorialMedicoUseCase useCase) {
        return new CreateHistorialMedicoService(useCase);
    }

    @Bean
    public FindHistorialMedicoUseCase findHistorialMedicoUseCase(HistorialMedicoRepository repository) {
        return new FindHistorialMedicoUseCase(repository);
    }

    @Bean
    public FindHistorialMedicoService findHistorialMedicoService(FindHistorialMedicoUseCase useCase) {
        return new FindHistorialMedicoService(useCase);
    }

    @Bean
    public DeleteHistorialMedicoUseCase deleteHistorialMedicoUseCase(HistorialMedicoRepository repository) {
        return new DeleteHistorialMedicoUseCase(repository);
    }

    @Bean
    public DeleteHistorialMedicoService deleteHistorialMedicoService(DeleteHistorialMedicoUseCase useCase) {
        return new DeleteHistorialMedicoService(useCase);
    }

    @Bean
    public EditHistorialMedicoUseCase editHistorialMedicoUseCase(HistorialMedicoRepository repository) {
        return new EditHistorialMedicoUseCase(repository);
    }

    @Bean
    public EditHistorialMedicoService editHistorialMedicoService(EditHistorialMedicoUseCase useCase) {
        return new EditHistorialMedicoService(useCase);
    }
}
