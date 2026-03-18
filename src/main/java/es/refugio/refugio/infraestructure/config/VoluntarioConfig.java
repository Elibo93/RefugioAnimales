package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.voluntario.CreateVoluntarioService;
import es.refugio.refugio.application.service.voluntario.DeleteVoluntarioService;
import es.refugio.refugio.application.service.voluntario.EditVoluntarioService;
import es.refugio.refugio.application.service.voluntario.FindVoluntarioService;
import es.refugio.refugio.application.usecase.voluntario.CreateVoluntarioUseCase;
import es.refugio.refugio.application.usecase.voluntario.DeleteVoluntarioUseCase;
import es.refugio.refugio.application.usecase.voluntario.EditVoluntarioUseCase;
import es.refugio.refugio.application.usecase.voluntario.FindVoluntarioUseCase;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.voluntario.VoluntarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.voluntario.VoluntarioJpaRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class VoluntarioConfig {

    private final VoluntarioEntityJpaRepository VoluntarioRepository;

    // Creo por configuración la instalacia que me interesa del productoRepository
    // (desde jpa)
    @Bean
    public VoluntarioRepository voluntarioRepository() {
        return new VoluntarioJpaRepositoryImpl(VoluntarioRepository);
    }

    // POST
    @Bean
    public CreateVoluntarioUseCase createVoluntarioUseCase() {
        return new CreateVoluntarioUseCase(voluntarioRepository());
    }

    @Bean
    public CreateVoluntarioService createVoluntarioService() {
        return new CreateVoluntarioService(createVoluntarioUseCase());
    }

    // GET
    @Bean
    public FindVoluntarioUseCase findVoluntarioUseCase() {
        return new FindVoluntarioUseCase(voluntarioRepository());
    }

    @Bean
    public FindVoluntarioService findVoluntarioService() {
        return new FindVoluntarioService(findVoluntarioUseCase());
    }

    // DELETE

    @Bean
    public DeleteVoluntarioUseCase deleteVoluntarioUseCase() {
        return new DeleteVoluntarioUseCase(voluntarioRepository());
    }

    @Bean
    public DeleteVoluntarioService deleteVoluntarioService() {
        return new DeleteVoluntarioService(deleteVoluntarioUseCase());
    }

    // PUT
    @Bean
    public EditVoluntarioUseCase editVoluntarioUseCase() {
        return new EditVoluntarioUseCase(voluntarioRepository());
    }

    @Bean
    public EditVoluntarioService editVoluntarioService() {
        return new EditVoluntarioService(editVoluntarioUseCase());
    }
}















