package es.refugio.animales.refugio.domain.repository;

import java.util.Optional;

import es.refugio.animales.refugio.domain.model.persona.Persona;
import es.refugio.animales.refugio.domain.model.persona.PersonaId;
import es.refugio.animales.common.domain.repository.CRUDRepository;

public interface PersonaRepository extends CRUDRepository<Persona, PersonaId> {

    public Optional<Persona> getByName(String name);

}
















