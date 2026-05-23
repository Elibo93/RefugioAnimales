package es.refugio.refugio.application.usecase.animal;

import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Delete Animal.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteAnimalUseCase {

    private final AnimalRepository animalRepository;

    public void delete(AnimalId id) {
        animalRepository.deleteById(id);
    }
}