package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.adoptante.ApproveAdoptanteService;
import es.refugio.refugio.application.service.adoptante.CreateAdoptanteService;
import es.refugio.refugio.application.service.adoptante.DeleteAdoptanteService;
import es.refugio.refugio.application.service.adoptante.EditAdoptanteService;
import es.refugio.refugio.application.service.adoptante.FindAdoptanteService;
import es.refugio.refugio.application.service.adoptante.RejectAdoptanteService;
import es.refugio.refugio.application.usecase.adoptante.ApproveAdoptanteUseCase;
import es.refugio.refugio.application.usecase.adoptante.CreateAdoptanteUseCase;
import es.refugio.refugio.application.usecase.adoptante.DeleteAdoptanteUseCase;
import es.refugio.refugio.application.usecase.adoptante.EditAdoptanteUseCase;
import es.refugio.refugio.application.usecase.adoptante.FindAdoptanteUseCase;
import es.refugio.refugio.application.usecase.adoptante.RejectAdoptanteUseCase;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.adoptante.AdoptanteEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.adoptante.AdoptanteJpaRepositoryImpl;
import es.refugio.refugio.infraestructure.mapper.AdoptanteMapper;

@Configuration
public class AdoptanteConfig {

    // --- REPOSITORIO ---
    @Bean
    public AdoptanteRepository adoptanteRepository(AdoptanteEntityJpaRepository jpaRepository, AdoptanteMapper mapper) {
        return new AdoptanteJpaRepositoryImpl(jpaRepository, mapper);
    }

    // --- CREAR (POST) ---
    @Bean
    public CreateAdoptanteUseCase createAdoptanteUseCase(AdoptanteRepository repository, PerfilLegalRepository perfilLegalRepository) {
        return new CreateAdoptanteUseCase(repository, perfilLegalRepository);
    }

    @Bean
    public CreateAdoptanteService createAdoptanteService(CreateAdoptanteUseCase useCase) {
        return new CreateAdoptanteService(useCase);
    }

    // --- BUSCAR/OBTENER (GET) ---
    @Bean
    public FindAdoptanteUseCase findAdoptanteUseCase(AdoptanteRepository repository) {
        return new FindAdoptanteUseCase(repository);
    }

    @Bean
    public FindAdoptanteService findAdoptanteService(FindAdoptanteUseCase useCase) {
        return new FindAdoptanteService(useCase);
    }

    // --- ELIMINAR (DELETE) ---
    @Bean
    public DeleteAdoptanteUseCase deleteAdoptanteUseCase(AdoptanteRepository repository) {
        return new DeleteAdoptanteUseCase(repository);
    }

    @Bean
    public DeleteAdoptanteService deleteAdoptanteService(DeleteAdoptanteUseCase useCase) {
        return new DeleteAdoptanteService(useCase);
    }

    // --- EDITAR (PUT) ---
    @Bean
    public EditAdoptanteUseCase editAdoptanteUseCase(AdoptanteRepository repository) {
        return new EditAdoptanteUseCase(repository);
    }

    @Bean
    public EditAdoptanteService editAdoptanteService(EditAdoptanteUseCase useCase) {
        return new EditAdoptanteService(useCase);
    }

    // --- APPROVE (PATCH) ---
    @Bean
    public ApproveAdoptanteUseCase approveAdoptanteUseCase(AdoptanteRepository repository) {
        return new ApproveAdoptanteUseCase(repository);
    }

    @Bean
    public ApproveAdoptanteService approveAdoptanteService(ApproveAdoptanteUseCase useCase) {
        return new ApproveAdoptanteService(useCase);
    }

    // --- REJECT (PATCH) ---
    @Bean
    public RejectAdoptanteUseCase rejectAdoptanteUseCase(AdoptanteRepository repository) {
        return new RejectAdoptanteUseCase(repository);
    }

    @Bean
    public RejectAdoptanteService rejectAdoptanteService(RejectAdoptanteUseCase useCase) {
        return new RejectAdoptanteService(useCase);
    }
}