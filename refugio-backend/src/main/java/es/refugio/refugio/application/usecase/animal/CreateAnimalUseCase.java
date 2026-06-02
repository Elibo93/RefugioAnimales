package es.refugio.refugio.application.usecase.animal;

import java.time.LocalDateTime;
import es.refugio.refugio.application.command.animal.CreateAnimalCommand;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.animal.enums.Sexo;
import es.refugio.refugio.domain.model.animal.enums.Tamano;
import es.refugio.refugio.domain.repository.AnimalRepository;
import lombok.AllArgsConstructor;

/**
 * Caso de uso que encapsula la creación de un nuevo animal en el sistema.
 * Construye el objeto de dominio {@link es.refugio.refugio.domain.model.animal.Animal} a partir
 * del comando recibido y lo persiste a través del repositorio.
 *
 * @author Elisabeth
 * @author Diego
 */
@AllArgsConstructor
public class CreateAnimalUseCase {

    private final AnimalRepository animalRepository;

    /**
     * Crea y persiste un nuevo animal a partir del comando de entrada.
     * Convierte los valores de enumerados desde String, construye el animal con todos sus
     * atributos (incluyendo especie personalizada si aplica) y lo delega al repositorio.
     *
     * @param comando Objeto que contiene todos los datos necesarios para registrar el animal.
     * @return El objeto {@link es.refugio.refugio.domain.model.animal.Animal} guardado con su ID asignado.
     * @throws IllegalArgumentException Si alguno de los valores de especie, sexo, estado o tamaño no es válido.
     */
    public Animal create(CreateAnimalCommand comando) {
        Especie especieEnum = Especie.valueOf(comando.especie().toUpperCase());

        Animal animal = Animal.builder()
                .nombre(comando.nombre())
                .especie(especieEnum)
                .especiePersonalizada(especieEnum == Especie.OTRO ? comando.especiePersonalizada() : null)
                .raza(comando.raza())
                .sexo(Sexo.valueOf(comando.sexo().toUpperCase()))
                .chipId(comando.chipId())
                .estado(EstadoAnimal.valueOf(comando.estado().toUpperCase()))
                .edad(comando.edad())
                .tamano(Tamano.valueOf(comando.tamano().toUpperCase()))
                .descripcion(comando.descripcion())
                .foto(comando.foto())
                .peso(comando.peso())
                .nivelEnergia(comando.nivelEnergia())
                .urgencia(comando.urgencia())
                .visitas(0)
                .fechaIngreso(LocalDateTime.now())
                .build();


        return animalRepository.save(animal);
    }
}