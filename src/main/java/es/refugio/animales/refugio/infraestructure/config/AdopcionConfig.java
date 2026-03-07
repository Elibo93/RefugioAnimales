package es.refugio.animales.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.animales.refugio.application.service.adopcion.CreateAdopcionService;
import es.refugio.animales.refugio.application.service.adopcion.DeleteAdopcionService;
import es.refugio.animales.refugio.application.service.adopcion.EditAdopcionService;
import es.refugio.animales.refugio.application.service.adopcion.FindAdopcionService;
import es.refugio.animales.refugio.application.usecase.adopcion.CreateAdopcionUseCase;
import es.refugio.animales.refugio.application.usecase.adopcion.DeleteAdopcionUseCase;
import es.refugio.animales.refugio.application.usecase.adopcion.EditAdopcionUseCase;
import es.refugio.animales.refugio.application.usecase.adopcion.FindAdopcionUseCase;
import es.refugio.animales.refugio.domain.repository.AdopcionRepository;
import es.refugio.animales.refugio.infraestructure.db.jpa.repository.adopcion.AdopcionEntityJpaRepository;
import es.refugio.animales.refugio.infraestructure.db.jpa.repository.adopcion.AdopcionJpaRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AdopcionConfig {

    private final AdopcionEntityJpaRepository AdopcionRepository;

    // Creo por configuración la instalacia que me interesa de AdopcionRepository (desde jpa)
    @Bean
    public AdopcionRepository adopcionRepository() {
        return new AdopcionJpaRepositoryImpl(AdopcionRepository);
    }
    // POST
    @Bean
    public CreateAdopcionUseCase createAdopcionUseCase() {
        return new CreateAdopcionUseCase(adopcionRepository());
    }

    @Bean
    public CreateAdopcionService createAdopcionService() {
        return new CreateAdopcionService(createAdopcionUseCase());
    }

    // GET
    @Bean
    public FindAdopcionUseCase findAdopcionUseCase() {
        return new FindAdopcionUseCase(adopcionRepository());
    }

    @Bean
    public FindAdopcionService findAdopcionService() {
        return new FindAdopcionService(findAdopcionUseCase());
    }

    // DELETE
    @Bean
    public DeleteAdopcionUseCase deleteAdopcionUseCase() {
        return new DeleteAdopcionUseCase(adopcionRepository());
    }

    @Bean
    public DeleteAdopcionService deleteAdopcionService() {
        return new DeleteAdopcionService(deleteAdopcionUseCase());
    }

    // PUT
    @Bean
    public EditAdopcionUseCase editAdopcionUseCase() {
        return new EditAdopcionUseCase(adopcionRepository());
    }

    @Bean
    public EditAdopcionService editAdopcionService() {
        return new EditAdopcionService(editAdopcionUseCase());
    }
}
















