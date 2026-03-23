package es.refugio.refugio.application.usecase.animal;

import java.util.List;
import es.refugio.refugio.domain.error.AnimalNotFoundException;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindAnimalUseCase {

    private final AnimalRepository animalRepository;

    public List<Animal> findAll() {
        List<Animal> animales = animalRepository.getAll();

        if (animales.isEmpty()) {
            throw new AnimalNotFoundException();
        }
        return animales;
    }

    public Animal findById(AnimalId id) {
        return animalRepository.getById(id)
                .orElseThrow(() -> new AnimalNotFoundException(id.getValue()));
    }

    public List<Animal> findByEstado(es.refugio.refugio.domain.model.animal.enums.EstadoAnimal estado) {
        return animalRepository.getByEstado(estado);
    }
}