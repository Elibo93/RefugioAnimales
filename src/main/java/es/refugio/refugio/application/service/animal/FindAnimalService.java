package es.refugio.refugio.application.service.animal;

import java.util.List;
import org.springframework.stereotype.Service;
import es.refugio.refugio.application.usecase.animal.FindAnimalUseCase;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FindAnimalService {

    private final FindAnimalUseCase findAnimalUseCase;

    public List<Animal> findAll() {
        return findAnimalUseCase.findAll();
    }

    public Animal findById(AnimalId id) {
        return findAnimalUseCase.findById(id);
    }
}