package es.refugio.refugio.application.usecase.animal;

import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteAnimalUseCase {

    private final AnimalRepository animalRepository;

    public void delete(AnimalId id) {
        animalRepository.deleteById(id);
    }
}