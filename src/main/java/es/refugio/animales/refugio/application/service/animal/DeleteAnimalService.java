package es.refugio.animales.refugio.application.service.animal;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.usecase.animal.DeleteAnimalUseCase;
import es.refugio.animales.refugio.domain.model.animal.AnimalId;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteAnimalService {

    private final DeleteAnimalUseCase deleteAnimalUseCase;

    public void delete(AnimalId id) {
        deleteAnimalUseCase.delete(id);
    }
}

















