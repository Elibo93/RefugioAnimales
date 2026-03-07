package es.refugio.animales.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.animales.refugio.application.service.animal.CreateAnimalService;
import es.refugio.animales.refugio.application.service.animal.DeleteAnimalService;
import es.refugio.animales.refugio.application.service.animal.EditAnimalService;
import es.refugio.animales.refugio.application.service.animal.FindAnimalService;
import es.refugio.animales.refugio.application.usecase.animal.CreateAnimalUseCase;
import es.refugio.animales.refugio.application.usecase.animal.DeleteAnimalUseCase;
import es.refugio.animales.refugio.application.usecase.animal.EditAnimalUseCase;
import es.refugio.animales.refugio.application.usecase.animal.FindAnimalUseCase;
import es.refugio.animales.refugio.domain.repository.AnimalRepository;
import es.refugio.animales.refugio.infraestructure.db.jpa.repository.animal.AnimalEntityJpaRepository;
import es.refugio.animales.refugio.infraestructure.db.jpa.repository.animal.AnimalJpaRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AnimalConfig {

    private final AnimalEntityJpaRepository AnimalRepository;

    // Creo por configuración la instalacia que me interesa del productoRepository
    // (desde jpa)
    @Bean
    public AnimalRepository animalRepository() {
        return new AnimalJpaRepositoryImpl(AnimalRepository);
    }

    // POST
    @Bean
    public CreateAnimalUseCase createAnimalUseCase() {
        return new CreateAnimalUseCase(animalRepository());
    }

    @Bean
    public CreateAnimalService createAnimalService() {
        return new CreateAnimalService(createAnimalUseCase());
    }

    // GET
    @Bean
    public FindAnimalUseCase findAnimalUseCase() {
        return new FindAnimalUseCase(animalRepository());
    }

    @Bean
    public FindAnimalService findAnimalService() {
        return new FindAnimalService(findAnimalUseCase());
    }

    @Bean
    public DeleteAnimalUseCase deleteAnimalUseCase() {
        return new DeleteAnimalUseCase(animalRepository());
    }

    @Bean
    public DeleteAnimalService deleteAnimalService() {
        return new DeleteAnimalService(deleteAnimalUseCase());
    }

    @Bean
    public EditAnimalUseCase editAnimalUseCase() {
        return new EditAnimalUseCase(animalRepository());
    }

    @Bean
    public EditAnimalService editAnimalService() {
        return new EditAnimalService(editAnimalUseCase());
    }
}
















