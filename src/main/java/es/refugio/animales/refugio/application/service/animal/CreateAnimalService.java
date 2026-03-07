package es.refugio.animales.refugio.application.service.animal;

import org.springframework.stereotype.Service;

import es.refugio.animales.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.animales.refugio.application.usecase.animal.CreateAnimalUseCase;
import es.refugio.animales.refugio.domain.model.animal.Animal;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreateAnimalService {

    //Atributos
    private final CreateAnimalUseCase createAnimalUseCase;

    public Animal createAnimal(CreateAnimalCommand command) {
        Animal animal = createAnimalUseCase.create(command);
        return animal;
    }
}

















