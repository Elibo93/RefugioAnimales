package es.refugio.refugio.application.usecase.animal;

import java.util.List;
import es.refugio.refugio.domain.error.AnimalNotFoundException;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    public Page<Animal> findAll(Pageable pageable) {
        return animalRepository.findAll(pageable);
    }

    public Animal findById(AnimalId id) {
        return animalRepository.getById(id)
                .orElseThrow(() -> new AnimalNotFoundException(id.getValue()));
    }

    public List<Animal> findByEstado(EstadoAnimal estado) {
        return animalRepository.getByEstado(estado);
    }

    public List<Animal> findFiltered(String q, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia) {
        return animalRepository.findFiltered(q, especie, tamano, edad, sexo, urgencia);
    }

    public Page<Animal> findFiltered(String q, String estado, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia, Pageable pageable) {
        return animalRepository.findFiltered(q, estado, especie, tamano, edad, sexo, urgencia, pageable);
    }

    public List<Animal> findTop3Favoritos() {
        return animalRepository.findTop3ByEstadoOrderByVisitasDesc(EstadoAnimal.DISPONIBLE);
    }
}