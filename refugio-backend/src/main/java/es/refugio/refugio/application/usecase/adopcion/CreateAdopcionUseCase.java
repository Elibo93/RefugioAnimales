package es.refugio.refugio.application.usecase.adopcion;

import es.refugio.refugio.application.command.adopcion.CreateAdopcionCommand;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.domain.error.AnimalYaAdoptadoException;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateAdopcionUseCase {

    private final AdopcionRepository adopcionRepository;

    public Adopcion create(CreateAdopcionCommand command) {
        AnimalId animalId = new AnimalId(command.animalId());
        
        if (adopcionRepository.existsByAnimalId(animalId)) {
            throw new AnimalYaAdoptadoException(command.animalId());
        }

        Adopcion adopcion = Adopcion.builder()
                .adoptanteId(new AdoptanteId(command.adoptanteId()))
                .animalId(animalId)
                .fechaAdopcion(LocalDateTime.now())
                .estado(command.estado())
                .contrato(command.contrato())
                .build();

        return adopcionRepository.save(adopcion);
    }
}
