package es.refugio.refugio.application.service.animal;

import org.springframework.stereotype.Service;
import es.refugio.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.refugio.application.usecase.animal.EditAnimalUseCase;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EditAnimalService {

    private final EditAnimalUseCase editAnimalUseCase;

    public Animal update(EditAnimalCommand command) {
        return editAnimalUseCase.update(command);
    }

    public void incrementVisitas(AnimalId id) {
        editAnimalUseCase.incrementarVisitas(id);
    }
}