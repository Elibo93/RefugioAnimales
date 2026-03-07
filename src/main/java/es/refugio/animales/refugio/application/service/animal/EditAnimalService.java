package es.refugio.animales.refugio.application.service.animal;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.animales.refugio.application.usecase.animal.EditAnimalUseCase;
import es.refugio.animales.refugio.domain.model.animal.Animal;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditAnimalService {

    private final EditAnimalUseCase editAnimalUseCase;

    public Animal update(EditAnimalCommand command) {
        return editAnimalUseCase.update(command);
    }
}

















