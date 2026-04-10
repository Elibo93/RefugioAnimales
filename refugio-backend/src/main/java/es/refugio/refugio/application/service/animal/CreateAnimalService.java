package es.refugio.refugio.application.service.animal;

import org.springframework.stereotype.Service;
import es.refugio.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.refugio.application.usecase.animal.CreateAnimalUseCase;
import es.refugio.refugio.domain.model.animal.Animal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreateAnimalService {

    private final CreateAnimalUseCase createAnimalUseCase;

    public Animal createAnimal(CreateAnimalCommand command) {
        return createAnimalUseCase.create(command);
    }
}