package es.refugio.refugio.application.usecase.adopcion;

import es.refugio.refugio.application.command.adopcion.EditAdopcionCommand;
import es.refugio.refugio.domain.error.AdopcionNotFoundException;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Edit Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class EditAdopcionUseCase {

    private final AdopcionRepository adopcionRepository;
    private final AnimalRepository animalRepository;

    @Transactional
    public Adopcion update(EditAdopcionCommand command) {
        return adopcionRepository.getById(command.id())
                .map(adopcion -> {
                    EstadoAdopcion estadoEnum = EstadoAdopcion.valueOf(command.estado().toUpperCase());
                    
                    adopcion.setAnimalId(new AnimalId(command.animalId()));
                    adopcion.setAdoptanteId(new AdoptanteId(command.adoptanteId()));
                    adopcion.setFechaAdopcion(command.fechaAdopcion());
                    adopcion.setEstado(estadoEnum);
                    adopcion.setContrato(command.contrato());
                    
                    if (estadoEnum != EstadoAdopcion.PENDIENTE_FIRMA && estadoEnum != EstadoAdopcion.CANCELADA) {
                        animalRepository.getById(adopcion.getAnimalId()).ifPresent(animal -> {
                            animal.setEstado(EstadoAnimal.ADOPTADO);
                            animalRepository.save(animal);
                        });
                    } else if (estadoEnum == EstadoAdopcion.CANCELADA) {
                        animalRepository.getById(adopcion.getAnimalId()).ifPresent(animal -> {
                            animal.setEstado(EstadoAnimal.DISPONIBLE);
                            animalRepository.save(animal);
                        });
                    }
                    
                    return adopcionRepository.save(adopcion);
                })
                .orElseThrow(() -> new AdopcionNotFoundException(command.id().getValue()));
    }
}
