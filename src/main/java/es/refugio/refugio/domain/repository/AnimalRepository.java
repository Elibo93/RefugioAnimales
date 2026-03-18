package es.refugio.refugio.domain.repository;

import java.util.List;
import java.util.Optional;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;

public interface AnimalRepository extends CRUDRepository<Animal, AnimalId> {

    Optional<Animal> getByChipId(String chipId);

    List<Animal> getByEstado(EstadoAnimal estado);

    List<Animal> getByEspecie(Especie especie);
}