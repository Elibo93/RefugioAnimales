package es.refugio.refugio.domain.repository;

import java.util.Optional;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

public interface VoluntarioRepository extends CRUDRepository<Voluntario, VoluntarioId> {
    Optional<Voluntario> findByUsuarioId(UsuarioId usuarioId);
}
