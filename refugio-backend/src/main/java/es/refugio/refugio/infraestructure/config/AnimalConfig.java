package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import es.refugio.refugio.application.usecase.animal.CreateAnimalUseCase;
import es.refugio.refugio.application.usecase.animal.DeleteAnimalUseCase;
import es.refugio.refugio.application.usecase.animal.EditAnimalUseCase;
import es.refugio.refugio.application.usecase.animal.FindAnimalUseCase;
import es.refugio.refugio.application.usecase.animal.IncrementarVisitasUseCase;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.animal.AnimalEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.animal.AnimalJpaRepositoryImpl;
import es.refugio.refugio.infraestructure.mapper.AnimalMapper;

@Configuration
public class AnimalConfig {

    @Bean
    public AnimalRepository animalRepository(AnimalEntityJpaRepository jpaRepository, AnimalMapper mapper) {
        return new AnimalJpaRepositoryImpl(jpaRepository, mapper);
    }

    @Bean
    public CreateAnimalUseCase createAnimalUseCase(AnimalRepository repository) {
        return new CreateAnimalUseCase(repository);
    }


    @Bean
    public FindAnimalUseCase findAnimalUseCase(AnimalRepository repository) {
        return new FindAnimalUseCase(repository);
    }


    @Bean
    public DeleteAnimalUseCase deleteAnimalUseCase(AnimalRepository repository) {
        return new DeleteAnimalUseCase(repository);
    }


    @Bean
    public EditAnimalUseCase editAnimalUseCase(AnimalRepository repository) {
        return new EditAnimalUseCase(repository);
    }


    @Bean
    public IncrementarVisitasUseCase incrementarVisitasUseCase(AnimalRepository repository) {
        return new IncrementarVisitasUseCase(repository);
    }


}