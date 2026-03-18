package es.refugio.animales.refugio.application.usecase.animal;

import es.refugio.animales.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.animales.refugio.domain.error.AnimalNotFoundException;
import es.refugio.animales.refugio.domain.model.animal.Animal;
import es.refugio.animales.refugio.domain.repository.AnimalRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditAnimalUseCase {

    private final AnimalRepository animalRepository;

    public Animal update(EditAnimalCommand command) {
        return animalRepository.getById(command.id())
                .map(t -> {
                    t.setNombre(command.nombre());
                    t.setEspecie(command.especie());
                    t.setRaza(command.raza());
                    t.setSexo(command.sexo());
                    t.setChipId(command.chipId());
                    t.setEdad(command.edad());
                    t.setTamano(command.tamano());
                    t.setDescripcion(command.descripcion());
                    t.setFoto(command.foto());
                    return animalRepository.save(t);
                })
                .orElseThrow(() -> new AnimalNotFoundException(command.id().getValue()));
    }
}
