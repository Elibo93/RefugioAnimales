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
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.infraestructure.db.jpa.repository.voluntario.VoluntarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.voluntario.VoluntarioJpaRepositoryImpl;

@Configuration
public class VoluntarioConfig {

    @Bean
    public VoluntarioRepository voluntarioRepository(VoluntarioEntityJpaRepository jpaRepository) {
        return new VoluntarioJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public CreateVoluntarioUseCase createVoluntarioUseCase(VoluntarioRepository repository,
            PerfilLegalRepository perfilLegalRepository, NotificacionService notificacionService) {
        return new CreateVoluntarioUseCase(repository, perfilLegalRepository, notificacionService);
    }

    @Bean
    public CreateVoluntarioService createVoluntarioService(CreateVoluntarioUseCase useCase) {
        return new CreateVoluntarioService(useCase);
    }

    @Bean
    public EditVoluntarioUseCase editVoluntarioUseCase(VoluntarioRepository repository) {
        return new EditVoluntarioUseCase(repository);
    }

    @Bean
    public EditVoluntarioService editVoluntarioService(EditVoluntarioUseCase useCase) {
        return new EditVoluntarioService(useCase);
    }

    @Bean
    public FindVoluntarioUseCase findVoluntarioUseCase(VoluntarioRepository repository) {
        return new FindVoluntarioUseCase(repository);
    }

    @Bean
    public FindVoluntarioService findVoluntarioService(FindVoluntarioUseCase useCase) {
        return new FindVoluntarioService(useCase);
    }

    @Bean
    public DeleteVoluntarioUseCase deleteVoluntarioUseCase(VoluntarioRepository repository) {
        return new DeleteVoluntarioUseCase(repository);
    }

    @Bean
    public DeleteVoluntarioService deleteVoluntarioService(DeleteVoluntarioUseCase useCase) {
        return new DeleteVoluntarioService(useCase);
    }
}
