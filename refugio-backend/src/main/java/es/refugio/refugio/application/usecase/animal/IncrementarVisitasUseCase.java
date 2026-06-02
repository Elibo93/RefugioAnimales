package es.refugio.refugio.application.usecase.animal;

import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Incrementar Visitas.
 *
 * @author Elisabeth
 * @author Diego
 */
public class IncrementarVisitasUseCase {
    private final AnimalRepository animalRepository;

    public void execute(AnimalId id) {
        animalRepository.incrementarVisitas(id);
    }
}
