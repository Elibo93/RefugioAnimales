package es.refugio.refugio.domain.repository;

import java.util.Optional;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

public interface AdoptanteRepository extends CRUDRepository<Adoptante, AdoptanteId> {

    Optional<Adoptante> getByDni(String dni);

    Optional<Adoptante> getByUsuarioId(UsuarioId usuarioId);

}