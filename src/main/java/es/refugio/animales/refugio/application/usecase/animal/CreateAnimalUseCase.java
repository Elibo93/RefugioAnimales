package es.refugio.animales.refugio.application.usecase.animal;

import es.refugio.animales.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.animales.refugio.domain.model.animal.Animal;
import es.refugio.animales.refugio.domain.repository.AnimalRepository;
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
                .estado(comando.estado())
                .notas(comando.notas())
                .createdAt(java.time.LocalDateTime.now())
                .build();

        return animalRepository.save(animal);

    }
}
