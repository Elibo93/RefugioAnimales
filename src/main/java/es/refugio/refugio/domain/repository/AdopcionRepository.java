package es.refugio.refugio.domain.repository;

import java.util.List;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;

public interface AdopcionRepository extends CRUDRepository<Adopcion, AdopcionId> {

    List<Adopcion> getByPersonaId(Integer PersonaId);

    List<Adopcion> getByAnimalId(Integer AnimalId);

    // Optional<Adopcion> getByPersonaAndAnimal(PersonaId personaId, AnimalId
    // AnimalId);

    // boolean existsByPersonaAndAnimal(PersonaId personaId, AnimalId AnimalId);

}
















