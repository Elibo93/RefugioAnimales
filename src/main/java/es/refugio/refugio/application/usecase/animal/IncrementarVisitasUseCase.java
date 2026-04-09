package es.refugio.refugio.application.usecase.animal;

import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IncrementarVisitasUseCase {
    private final AnimalRepository animalRepository;

    public void execute(AnimalId id) {
        animalRepository.incrementarVisitas(id);
    }
}
