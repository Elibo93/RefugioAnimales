package es.refugio.refugio.application.service.animal;

import org.springframework.stereotype.Service;
import es.refugio.refugio.application.usecase.animal.DeleteAnimalUseCase;
import es.refugio.refugio.domain.model.animal.AnimalId;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Delete Animal.
 *
 * @author Elisabeth
 * @author Diego
 */
public class DeleteAnimalService {

    private final DeleteAnimalUseCase deleteAnimalUseCase;

    public void delete(AnimalId id) {
        deleteAnimalUseCase.delete(id);
    }
}