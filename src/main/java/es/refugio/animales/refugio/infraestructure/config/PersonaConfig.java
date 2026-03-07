package es.refugio.animales.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.animales.refugio.application.service.persona.CreatePersonaService;
import es.refugio.animales.refugio.application.service.persona.DeletePersonaService;
import es.refugio.animales.refugio.application.service.persona.EditPersonaService;
import es.refugio.animales.refugio.application.service.persona.FindPersonaService;
import es.refugio.animales.refugio.application.usecase.persona.CreatePersonaUseCase;
import es.refugio.animales.refugio.application.usecase.persona.DeletePersonaUseCase;
import es.refugio.animales.refugio.application.usecase.persona.EditPersonaUseCase;
import es.refugio.animales.refugio.application.usecase.persona.FindPersonaUseCase;
import es.refugio.animales.refugio.domain.repository.PersonaRepository;
import es.refugio.animales.refugio.infraestructure.db.jpa.repository.persona.PersonaEntityJpaRepository;
import es.refugio.animales.refugio.infraestructure.db.jpa.repository.persona.PersonaJpaRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PersonaConfig {

    private final PersonaEntityJpaRepository PersonaRepository;

    @Bean
    public PersonaRepository personaRepository() {
        return new PersonaJpaRepositoryImpl(PersonaRepository);
    }

    // POST
    @Bean
    public CreatePersonaUseCase createPersonaUseCase() {
        return new CreatePersonaUseCase(personaRepository());
    }

    @Bean
    public CreatePersonaService createPersonaService() {
        return new CreatePersonaService(createPersonaUseCase());
    }

    // GET
    @Bean
    public FindPersonaUseCase findPersonaUseCase() {
        return new FindPersonaUseCase(personaRepository());
    }

    @Bean
    public FindPersonaService findPersonaService() {
        return new FindPersonaService(findPersonaUseCase());
    }

    // DELETE

    @Bean
    public DeletePersonaUseCase deletePersonaUseCase() {
        return new DeletePersonaUseCase(personaRepository());
    }

    @Bean
    public DeletePersonaService deletePersonaService() {
        return new DeletePersonaService(deletePersonaUseCase());
    }

    // PUT
    @Bean
    public EditPersonaUseCase editPersonaUseCase() {
        return new EditPersonaUseCase(personaRepository());
    }

    @Bean
    public EditPersonaService editPersonaService() {
        return new EditPersonaService(editPersonaUseCase());
    }
}
