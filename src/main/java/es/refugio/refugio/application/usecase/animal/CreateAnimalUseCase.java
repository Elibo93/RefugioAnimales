package es.refugio.refugio.application.usecase.animal;

import es.refugio.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateAnimalUseCase {

    private final AnimalRepository animalRepository;

    public Animal create(CreateAnimalCommand comando) {

        Animal animal = Animal.builder()
                .nombre(comando.nombre())
                .especie(comando.especie())
                .raza(comando.raza())
                .sexo(comando.sexo())
                .chipId(comando.chipId())
                .edad(comando.edad())
                .tamano(comando.tamano())
                .descripcion(comando.descripcion())
                .foto(comando.foto())
                .fechaIngreso(java.time.LocalDateTime.now())
                .build();

        return animalRepository.save(animal);

    }
}
