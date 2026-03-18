package es.refugio.animales.refugio.domain.repository;

import java.util.Optional;

import es.refugio.animales.common.domain.repository.CRUDRepository;
import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.domain.model.usuario.PersonaId;

public interface PersonaRepository extends CRUDRepository<Persona, PersonaId> {

    public Optional<Persona> getByName(String name);

}
