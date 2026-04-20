package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.solicitud_adopcion.AprobarSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.CreateSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.DeleteSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.EditSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.FindSolicitudAdopcionService;
import es.refugio.refugio.application.service.solicitud_adopcion.RechazarSolicitudAdopcionService;
import es.refugio.refugio.application.usecase.solicitud_adopcion.AprobarSolicitudAdopcionUseCase;
import es.refugio.refugio.application.usecase.solicitud_adopcion.CreateSolicitudAdopcionUseCase;
import es.refugio.refugio.application.usecase.solicitud_adopcion.DeleteSolicitudAdopcionUseCase;
import es.refugio.refugio.application.usecase.solicitud_adopcion.EditSolicitudAdopcionUseCase;
import es.refugio.refugio.application.usecase.solicitud_adopcion.FindSolicitudAdopcionUseCase;
import es.refugio.refugio.application.usecase.solicitud_adopcion.RechazarSolicitudAdopcionUseCase;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.solicitud_adopcion.SolicitudAdopcionEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.solicitud_adopcion.SolicitudAdopcionJpaRepositoryImpl;

@Configuration
public class SolicitudAdopcionConfig {

    @Bean
    public SolicitudAdopcionRepository solicitudAdopcionRepository(SolicitudAdopcionEntityJpaRepository jpaRepository) {
        return new SolicitudAdopcionJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public CreateSolicitudAdopcionUseCase createSolicitudAdopcionUseCase(SolicitudAdopcionRepository repository,
            AnimalRepository animalRepository) {
        return new CreateSolicitudAdopcionUseCase(repository, animalRepository);
    }

    @Bean
    public CreateSolicitudAdopcionService createSolicitudAdopcionService(CreateSolicitudAdopcionUseCase useCase) {
        return new CreateSolicitudAdopcionService(useCase);
    }

    @Bean
    public EditSolicitudAdopcionUseCase editSolicitudAdopcionUseCase(SolicitudAdopcionRepository repository) {
        return new EditSolicitudAdopcionUseCase(repository);
    }

    @Bean
    public EditSolicitudAdopcionService editSolicitudAdopcionService(EditSolicitudAdopcionUseCase useCase) {
        return new EditSolicitudAdopcionService(useCase);
    }

    @Bean
    public FindSolicitudAdopcionUseCase findSolicitudAdopcionUseCase(SolicitudAdopcionRepository repository) {
        return new FindSolicitudAdopcionUseCase(repository);
    }

    @Bean
    public FindSolicitudAdopcionService findSolicitudAdopcionService(FindSolicitudAdopcionUseCase useCase) {
        return new FindSolicitudAdopcionService(useCase);
    }

    @Bean
    public DeleteSolicitudAdopcionUseCase deleteSolicitudAdopcionUseCase(SolicitudAdopcionRepository repository) {
        return new DeleteSolicitudAdopcionUseCase(repository);
    }

    @Bean
    public DeleteSolicitudAdopcionService deleteSolicitudAdopcionService(DeleteSolicitudAdopcionUseCase useCase) {
        return new DeleteSolicitudAdopcionService(useCase);
    }

    @Bean
    public AprobarSolicitudAdopcionUseCase aprobarSolicitudAdopcionUseCase(
            SolicitudAdopcionRepository solicitudRepo,
            AnimalRepository animalRepo,
            AdoptanteRepository adoptanteRepo,
            AdopcionRepository adopcionRepo) {
        return new AprobarSolicitudAdopcionUseCase(solicitudRepo, animalRepo, adoptanteRepo, adopcionRepo);
    }

    @Bean
    public AprobarSolicitudAdopcionService aprobarSolicitudAdopcionService(AprobarSolicitudAdopcionUseCase useCase) {
        return new AprobarSolicitudAdopcionService(useCase);
    }

    @Bean
    public RechazarSolicitudAdopcionUseCase rechazarSolicitudAdopcionUseCase(
            SolicitudAdopcionRepository solicitudRepo,
            AdoptanteRepository adoptanteRepo) {
        return new RechazarSolicitudAdopcionUseCase(solicitudRepo, adoptanteRepo);
    }

    @Bean
    public RechazarSolicitudAdopcionService rechazarSolicitudAdopcionService(RechazarSolicitudAdopcionUseCase useCase) {
        return new RechazarSolicitudAdopcionService(useCase);
    }
}
