package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.adopcion.CreateAdopcionService;
import es.refugio.refugio.application.service.adopcion.DeleteAdopcionService;
import es.refugio.refugio.application.service.adopcion.EditAdopcionService;
import es.refugio.refugio.application.service.adopcion.FindAdopcionService;
import es.refugio.refugio.application.usecase.adopcion.CreateAdopcionUseCase;
import es.refugio.refugio.application.usecase.adopcion.DeleteAdopcionUseCase;
import es.refugio.refugio.application.usecase.adopcion.EditAdopcionUseCase;
import es.refugio.refugio.application.usecase.adopcion.FindAdopcionUseCase;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.adopcion.AdopcionEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.adopcion.AdopcionJpaRepositoryImpl;

@Configuration
public class AdopcionConfig {

    @Bean
    public AdopcionRepository adopcionRepository(AdopcionEntityJpaRepository jpaRepository) {
        return new AdopcionJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public CreateAdopcionUseCase createAdopcionUseCase(AdopcionRepository repository) {
        return new CreateAdopcionUseCase(repository);
    }

    @Bean
    public CreateAdopcionService createAdopcionService(CreateAdopcionUseCase useCase) {
        return new CreateAdopcionService(useCase);
    }

    @Bean
    public EditAdopcionUseCase editAdopcionUseCase(AdopcionRepository repository) {
        return new EditAdopcionUseCase(repository);
    }

    @Bean
    public EditAdopcionService editAdopcionService(EditAdopcionUseCase useCase) {
        return new EditAdopcionService(useCase);
    }

    @Bean
    public FindAdopcionUseCase findAdopcionUseCase(AdopcionRepository repository) {
        return new FindAdopcionUseCase(repository);
    }

    @Bean
    public FindAdopcionService findAdopcionService(FindAdopcionUseCase useCase) {
        return new FindAdopcionService(useCase);
    }

    @Bean
    public DeleteAdopcionUseCase deleteAdopcionUseCase(AdopcionRepository repository) {
        return new DeleteAdopcionUseCase(repository);
    }

    @Bean
    public DeleteAdopcionService deleteAdopcionService(DeleteAdopcionUseCase useCase) {
        return new DeleteAdopcionService(useCase);
    }
}
