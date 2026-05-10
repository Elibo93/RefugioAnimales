package es.refugio.refugio.domain.repository;

import java.util.List;
import java.util.Optional;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;

public interface AdopcionRepository extends CRUDRepository<Adopcion, AdopcionId> {

    List<Adopcion> getByAdoptanteId(AdoptanteId adoptanteId);

    List<Adopcion> getByAnimalId(AnimalId animalId);

    Optional<Adopcion> getByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId);

    boolean existsByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId);

    List<Adopcion> findByCriteria(AdoptanteId adoptanteId, AnimalId animalId);
    boolean existsByAnimalId(AnimalId animalId);
}