package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.usuario.CreateUsuarioService;
import es.refugio.refugio.application.service.usuario.DeleteUsuarioService;
import es.refugio.refugio.application.service.usuario.EditUsuarioService;
import es.refugio.refugio.application.service.usuario.FindUsuarioService;
import es.refugio.refugio.application.usecase.usuario.CreateUsuarioUseCase;
import es.refugio.refugio.application.usecase.usuario.DeleteUsuarioUseCase;
import es.refugio.refugio.application.usecase.usuario.EditUsuarioUseCase;
import es.refugio.refugio.application.usecase.usuario.FindUsuarioUseCase;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioJpaRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class UsuarioConfig {

    private final UsuarioEntityJpaRepository PersonaRepository;

    @Bean
    public UsuarioRepository personaRepository() {
        return new UsuarioJpaRepositoryImpl(PersonaRepository);
    }

    // POST
    @Bean
    public CreateUsuarioUseCase createPersonaUseCase() {
        return new CreateUsuarioUseCase(personaRepository());
    }

    @Bean
    public CreateUsuarioService createPersonaService() {
        return new CreateUsuarioService(createPersonaUseCase());
    }

    // GET
    @Bean
    public FindUsuarioUseCase findPersonaUseCase() {
        return new FindUsuarioUseCase(personaRepository());
    }

    @Bean
    public FindUsuarioService findPersonaService() {
        return new FindUsuarioService(findPersonaUseCase());
    }

    // DELETE

    @Bean
    public DeleteUsuarioUseCase deletePersonaUseCase() {
        return new DeleteUsuarioUseCase(personaRepository());
    }

    @Bean
    public DeleteUsuarioService deletePersonaService() {
        return new DeleteUsuarioService(deletePersonaUseCase());
    }

    // PUT
    @Bean
    public EditUsuarioUseCase editPersonaUseCase() {
        return new EditUsuarioUseCase(personaRepository());
    }

    @Bean
    public EditUsuarioService editPersonaService() {
        return new EditUsuarioService(editPersonaUseCase());
    }
}
