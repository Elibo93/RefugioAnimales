package es.refugio.animales.refugio.application.usecase.animal;

import java.util.List;

import es.refugio.animales.refugio.domain.error.AnimalNotFoundException;
import es.refugio.animales.refugio.domain.model.animal.Animal;
import es.refugio.animales.refugio.domain.model.animal.AnimalId;
import es.refugio.animales.refugio.domain.repository.AnimalRepository;
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
        return animalRepository.getById(id).orElseThrow(() -> new AnimalNotFoundException());
    }
}

















