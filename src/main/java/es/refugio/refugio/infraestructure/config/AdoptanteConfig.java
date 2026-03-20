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
import es.refugio.refugio.infraestructure.db.jpa.repository.adoptante.AdoptanteEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.adoptante.AdoptanteJpaRepositoryImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AdoptanteConfig {

    private final AdoptanteEntityJpaRepository adoptanteEntityJpaRepository;

    // --- REPOSITORIO ---
    @Bean
    public AdoptanteRepository adoptanteRepository() {
        return new AdoptanteJpaRepositoryImpl(adoptanteEntityJpaRepository);
    }

    // --- CREATE (POST) ---
    @Bean
    public CreateAdoptanteUseCase createAdoptanteUseCase() {
        return new CreateAdoptanteUseCase(adoptanteRepository());
    }

    @Bean
    public CreateAdoptanteService createAdoptanteService() {
        return new CreateAdoptanteService(createAdoptanteUseCase());
    }

    // --- FIND (GET) ---
    @Bean
    public FindAdoptanteUseCase findAdoptanteUseCase() {
        return new FindAdoptanteUseCase(adoptanteRepository());
    }

    @Bean
    public FindAdoptanteService findAdoptanteService() {
        return new FindAdoptanteService(findAdoptanteUseCase());
    }

    // --- DELETE ---
    @Bean
    public DeleteAdoptanteUseCase deleteAdoptanteUseCase() {
        return new DeleteAdoptanteUseCase(adoptanteRepository());
    }

    @Bean
    public DeleteAdoptanteService deleteAdoptanteService() {
        return new DeleteAdoptanteService(deleteAdoptanteUseCase());
    }

    // --- EDIT (PUT) ---
    @Bean
    public EditAdoptanteUseCase editAdoptanteUseCase() {
        return new EditAdoptanteUseCase(adoptanteRepository());
    }

    @Bean
    public EditAdoptanteService editAdoptanteService() {
        return new EditAdoptanteService(editAdoptanteUseCase());
    }

    // --- APPROVE (PATCH) ---
    @Bean
    public ApproveAdoptanteUseCase approveAdoptanteUseCase() {
        return new ApproveAdoptanteUseCase(adoptanteRepository());
    }

    @Bean
    public ApproveAdoptanteService approveAdoptanteService() {
        return new ApproveAdoptanteService(approveAdoptanteUseCase());
    }

    // --- REJECT (PATCH) ---
    @Bean
    public RejectAdoptanteUseCase rejectAdoptanteUseCase() {
        return new RejectAdoptanteUseCase(adoptanteRepository());
    }

    @Bean
    public RejectAdoptanteService rejectAdoptanteService() {
        return new RejectAdoptanteService(rejectAdoptanteUseCase());
    }
}