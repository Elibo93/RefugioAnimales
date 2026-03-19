package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.tarea.CreateTareaService;
import es.refugio.refugio.application.service.tarea.DeleteTareaService;
import es.refugio.refugio.application.service.tarea.EditTareaService;
import es.refugio.refugio.application.service.tarea.FindTareaService;
import es.refugio.refugio.application.usecase.tarea.CreateTareaUseCase;
import es.refugio.refugio.application.usecase.tarea.DeleteTareaUseCase;
import es.refugio.refugio.application.usecase.tarea.EditTareaUseCase;
import es.refugio.refugio.application.usecase.tarea.FindTareaUseCase;
import es.refugio.refugio.domain.repository.TareaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.tarea.TareaEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.tarea.TareaJpaRepositoryImpl;

@Configuration
public class TareaConfig {

    @Bean
    public TareaRepository tareaRepository(TareaEntityJpaRepository jpaRepository) {
        return new TareaJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public CreateTareaUseCase createTareaUseCase(TareaRepository repository) {
        return new CreateTareaUseCase(repository);
    }

    @Bean
    public CreateTareaService createTareaService(CreateTareaUseCase useCase) {
        return new CreateTareaService(useCase);
    }

    @Bean
    public EditTareaUseCase editTareaUseCase(TareaRepository repository) {
        return new EditTareaUseCase(repository);
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
}
