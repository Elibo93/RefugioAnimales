package es.refugio.refugio.application.service.animal;

import org.springframework.stereotype.Service;
import es.refugio.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.refugio.application.usecase.animal.CreateAnimalUseCase;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.application.service.preferencia.MatchingService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreateAnimalService {

    private final CreateAnimalUseCase createAnimalUseCase;
    private final MatchingService matchingService;

    public Animal createAnimal(CreateAnimalCommand command) {
        Animal animal = createAnimalUseCase.create(command);
        matchingService.processNewAnimal(animal);
        return animal;
    }
}