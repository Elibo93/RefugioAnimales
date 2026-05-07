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
import es.refugio.refugio.application.usecase.donacion.CreateObjetivoDonacionUseCase;
import es.refugio.refugio.application.usecase.donacion.FindObjetivoDonacionUseCase;
import es.refugio.refugio.domain.repository.DonacionRepository;
import es.refugio.refugio.domain.repository.ObjetivoDonacionRepository;

import es.refugio.refugio.infraestructure.db.jpa.repository.donacion.DonacionEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.donacion.DonacionJpaRepositoryImpl;
import es.refugio.refugio.infraestructure.db.jpa.repository.donacion.ObjetivoDonacionEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.repository.donacion.ObjetivoDonacionJpaRepositoryImpl;
import es.refugio.refugio.infraestructure.mapper.ObjetivoDonacionMapper;

@Configuration
public class DonacionConfig {

    @Bean
    public DonacionRepository donacionRepository(DonacionEntityJpaRepository jpaRepository) {
        return new DonacionJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public ObjetivoDonacionRepository objetivoDonacionRepository(
            ObjetivoDonacionEntityJpaRepository jpaRepository,
            ObjetivoDonacionMapper mapper) {
        return new ObjetivoDonacionJpaRepositoryImpl(jpaRepository);
    }

    @Bean
    public CreateDonacionUseCase createDonacionUseCase(
            DonacionRepository repository,
            ObjetivoDonacionRepository objetivoRepository) {
        return new CreateDonacionUseCase(repository, objetivoRepository);
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

    @Bean
    public CreateObjetivoDonacionUseCase createObjetivoDonacionUseCase(ObjetivoDonacionRepository repository) {
        return new CreateObjetivoDonacionUseCase(repository);
    }

    @Bean
    public FindObjetivoDonacionUseCase findObjetivoDonacionUseCase(ObjetivoDonacionRepository repository) {
        return new FindObjetivoDonacionUseCase(repository);
    }
}
