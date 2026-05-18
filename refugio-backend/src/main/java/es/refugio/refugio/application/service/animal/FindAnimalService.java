package es.refugio.refugio.application.service.animal;

import java.util.List;
import org.springframework.stereotype.Service;
import es.refugio.refugio.application.usecase.animal.FindAnimalUseCase;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Service
public class FindAnimalService {

    private final FindAnimalUseCase findAnimalUseCase;

    public List<Animal> findAll() {
        return findAnimalUseCase.findAll();
    }

    public Page<Animal> findAll(Pageable pageable) {
        return findAnimalUseCase.findAll(pageable);
    }

    public Animal findById(AnimalId id) {
        return findAnimalUseCase.findById(id);
    }

    public List<Animal> findByStatus(EstadoAnimal status) {
        return findAnimalUseCase.findByEstado(status);
    }

    public List<Animal> findFiltered(String q, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia) {
        return findAnimalUseCase.findFiltered(q, especie, tamano, edad, sexo, urgencia);
    }

    public Page<Animal> findFiltered(String q, String estado, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia, Pageable pageable) {
        return findAnimalUseCase.findFiltered(q, estado, especie, tamano, edad, sexo, urgencia, pageable);
    }

    public List<Animal> findTop3Favoritos() {
        return findAnimalUseCase.findTop3Favoritos();
    }
}