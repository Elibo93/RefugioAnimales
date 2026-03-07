package es.refugio.animales.refugio.domain.repository;

import java.util.Optional;

import es.refugio.animales.refugio.domain.model.voluntario.Voluntario;
import es.refugio.animales.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.animales.common.domain.repository.CRUDRepository;

public interface VoluntarioRepository extends CRUDRepository<Voluntario, VoluntarioId> {

    public Optional<Voluntario> getByName(String name);

}
















