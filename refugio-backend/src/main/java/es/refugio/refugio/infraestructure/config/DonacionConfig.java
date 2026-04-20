package es.refugio.refugio.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.refugio.refugio.application.service.donacion.CreateDonacionService;
import es.refugio.refugio.application.service.donacion.DeleteDonacionService;
import es.refugio.refugio.application.service.donacion.EditDonacionService;
import es.refugio.refugio.application.service.donacion.FindDonacionService;
import es.refugio.refugio.application.usecase.donacion.CreateDonacionUseCase;
import es.refugio.refugio.application.usecase.donacion.DeleteDonacionUseCase;
import es.refugio.refugio.application.usecase.donacion.EditDonacionUseCase;
import es.refugio.refugio.application.usecase.donacion.FindDonacionUseCase;
import es.refugio.refugio.domain.repository.DonacionRepository;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.donacion.DonacionEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.donacion.DonacionJpaRepositoryImpl;

@Configuration
public class DonacionConfig {

    @Bean
    public DonacionRepository donacionRepository(DonacionEntityJpaRepository jpaRepository) {
        return new DonacionJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public CreateDonacionUseCase createDonacionUseCase(DonacionRepository repository, UsuarioRepository usuarioRepository) {
        return new CreateDonacionUseCase(repository, usuarioRepository);
    }


    @Bean
    public CreateDonacionService createDonacionService(CreateDonacionUseCase useCase) {
        return new CreateDonacionService(useCase);
    }

    @Bean
    public EditDonacionUseCase editDonacionUseCase(DonacionRepository repository) {
        return new EditDonacionUseCase(repository);
    }

    @Bean
    public EditDonacionService editDonacionService(EditDonacionUseCase useCase) {
        return new EditDonacionService(useCase);
    }

    @Bean
    public FindDonacionUseCase findDonacionUseCase(DonacionRepository repository) {
        return new FindDonacionUseCase(repository);
    }

    @Bean
    public FindDonacionService findDonacionService(FindDonacionUseCase useCase) {
        return new FindDonacionService(useCase);
    }

    @Bean
    public DeleteDonacionUseCase deleteDonacionUseCase(DonacionRepository repository) {
        return new DeleteDonacionUseCase(repository);
    }

    @Bean
    public DeleteDonacionService deleteDonacionService(DeleteDonacionUseCase useCase) {
        return new DeleteDonacionService(useCase);
    }
}
