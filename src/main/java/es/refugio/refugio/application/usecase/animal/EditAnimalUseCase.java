package es.refugio.refugio.application.usecase.animal;

import es.refugio.refugio.application.command.animal.EditAnimalCommand;
import es.refugio.refugio.domain.error.AnimalNotFoundException;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.animal.enums.Sexo;
import es.refugio.refugio.domain.model.animal.enums.Tamano;
import es.refugio.refugio.domain.model.animal.enums.Tamano;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EditAnimalUseCase {

    private final AnimalRepository animalRepository;

    public Animal update(EditAnimalCommand command) {
        return animalRepository.getById(command.id())
                .map(t -> {
                    Especie especieEnum = Especie.valueOf(command.especie().toUpperCase());
                    
                    t.setNombre(command.nombre());
                    t.setEspecie(especieEnum);
                    t.setEspeciePersonalizada(especieEnum == Especie.OTRO ? command.especiePersonalizada() : null);
                    t.setRaza(command.raza());
                    t.setSexo(Sexo.valueOf(command.sexo().toUpperCase()));
                    t.setChipId(command.chipId());
                    t.setEstado(EstadoAnimal.valueOf(command.estado().toUpperCase()));
                    t.setEdad(command.edad());
                    t.setTamano(Tamano.valueOf(command.tamano().toUpperCase()));
                    t.setDescripcion(command.descripcion());
                    t.setFoto(command.foto());
                    
                    
                    return animalRepository.save(t);
                })
                .orElseThrow(() -> new AnimalNotFoundException(command.id().getValue()));
    }
}