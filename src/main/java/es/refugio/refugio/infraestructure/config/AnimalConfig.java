package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.animal.CreateAnimalService;
import es.refugio.refugio.application.service.animal.DeleteAnimalService;
import es.refugio.refugio.application.service.animal.EditAnimalService;
import es.refugio.refugio.application.service.animal.FindAnimalService;
import es.refugio.refugio.application.usecase.animal.CreateAnimalUseCase;
import es.refugio.refugio.application.usecase.animal.DeleteAnimalUseCase;
import es.refugio.refugio.application.usecase.animal.EditAnimalUseCase;
import es.refugio.refugio.application.usecase.animal.FindAnimalUseCase;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.animal.AnimalEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.animal.AnimalJpaRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AnimalConfig {

    private final AnimalEntityJpaRepository jpaRepository;

    @Bean
    public AnimalRepository animalRepository() {
        return new AnimalJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public CreateAnimalUseCase createAnimalUseCase() {
        return new CreateAnimalUseCase(animalRepository());
    }

    @Bean
    public CreateAnimalService createAnimalService() {
        return new CreateAnimalService(createAnimalUseCase());
    }

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