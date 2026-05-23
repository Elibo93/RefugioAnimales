package es.refugio.refugio.infraestructure.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.NotificacionService;
import es.refugio.refugio.application.service.tarea.CreateTareaService;
import es.refugio.refugio.application.service.tarea.DeleteTareaService;
import es.refugio.refugio.application.service.tarea.EditTareaService;
import es.refugio.refugio.application.service.tarea.FindTareaService;
import es.refugio.refugio.application.service.tarea.FindTareaHistorialService;
import es.refugio.refugio.application.usecase.tarea.CreateTareaUseCase;
import es.refugio.refugio.application.usecase.tarea.DeleteTareaUseCase;
import es.refugio.refugio.application.usecase.tarea.EditTareaUseCase;
import es.refugio.refugio.application.usecase.tarea.FindTareaUseCase;
import es.refugio.refugio.application.usecase.tarea.FindTareaHistorialUseCase;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.domain.repository.TareaRepository;
import es.refugio.refugio.domain.repository.TareaHistorialRepository;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.tarea.TareaEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.tarea.TareaJpaRepositoryImpl;
import es.refugio.refugio.infraestructure.db.jpa.repository.tarea.TareaHistorialEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.tarea.TareaHistorialJpaRepositoryImpl;
import es.refugio.refugio.infraestructure.mapper.TareaMapper;
import es.refugio.refugio.infraestructure.mapper.TareaHistorialMapper;

@Configuration
public class TareaConfig {

    @Bean
    public TareaRepository tareaRepository(TareaEntityJpaRepository jpaRepository, TareaMapper mapper) {
        return new TareaJpaRepositoryImpl(jpaRepository, mapper);
    }

    @Bean
    public CreateTareaUseCase createTareaUseCase(TareaRepository repository,
            VoluntarioRepository voluntarioRepository,
            NotificacionService notificacionService,
            ApplicationEventPublisher eventPublisher) {
        return new CreateTareaUseCase(repository, voluntarioRepository, notificacionService, eventPublisher);
    }

    @Bean
    public CreateTareaService createTareaService(CreateTareaUseCase useCase) {
        return new CreateTareaService(useCase);
    }

    @Bean
    public EditTareaUseCase editTareaUseCase(TareaRepository repository,
            VoluntarioRepository voluntarioRepository,
            PerfilLegalRepository perfilLegalRepository,
            NotificacionService notificacionService,
            ApplicationEventPublisher eventPublisher) {
        return new EditTareaUseCase(repository, voluntarioRepository, perfilLegalRepository, notificacionService, eventPublisher);
    }

    @Bean
    public EditTareaService editTareaService(EditTareaUseCase useCase) {
        return new EditTareaService(useCase);
    }

    @Bean
    public FindTareaUseCase findTareaUseCase(TareaRepository repository) {
        return new FindTareaUseCase(repository);
    }

    @Bean
    public FindTareaService findTareaService(FindTareaUseCase useCase) {
        return new FindTareaService(useCase);
    }

    @Bean
    public DeleteTareaUseCase deleteTareaUseCase(TareaRepository repository) {
        return new DeleteTareaUseCase(repository);
    }

    @Bean
    public DeleteTareaService deleteTareaService(DeleteTareaUseCase useCase) {
        return new DeleteTareaService(useCase);
    }

    @Bean
    public TareaHistorialRepository tareaHistorialRepository(
            TareaHistorialEntityJpaRepository jpaRepository, TareaHistorialMapper mapper) {
        return new TareaHistorialJpaRepositoryImpl(jpaRepository, mapper);
    }

    @Bean
    public FindTareaHistorialUseCase findTareaHistorialUseCase(
            TareaHistorialRepository repository) {
        return new FindTareaHistorialUseCase(repository);
    }

    @Bean
    public FindTareaHistorialService findTareaHistorialService(
            FindTareaHistorialUseCase useCase) {
        return new FindTareaHistorialService(useCase);
    }
}