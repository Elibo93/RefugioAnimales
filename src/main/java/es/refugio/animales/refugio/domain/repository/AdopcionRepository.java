package es.refugio.animales.refugio.domain.repository;

import java.util.List;
import es.refugio.animales.refugio.domain.model.adopcion.Adopcion;
import es.refugio.animales.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.animales.common.domain.repository.CRUDRepository;

public interface AdopcionRepository extends CRUDRepository<Adopcion, AdopcionId> {

    List<Adopcion> getByPersonaId(Integer PersonaId);

    List<Adopcion> getByAnimalId(Integer AnimalId);

    // Optional<Adopcion> getByPersonaAndAnimal(PersonaId personaId, AnimalId
    // AnimalId);

    // boolean existsByPersonaAndAnimal(PersonaId personaId, AnimalId AnimalId);

}
















