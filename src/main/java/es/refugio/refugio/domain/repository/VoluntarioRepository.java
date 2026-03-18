package es.refugio.refugio.domain.repository;

import java.util.Optional;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;

public interface VoluntarioRepository extends CRUDRepository<Voluntario, VoluntarioId> {

    public Optional<Voluntario> getByName(String name);

}
















