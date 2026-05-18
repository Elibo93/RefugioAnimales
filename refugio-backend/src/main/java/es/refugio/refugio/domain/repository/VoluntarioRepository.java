package es.refugio.refugio.domain.repository;

import java.util.Optional;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoluntarioRepository extends CRUDRepository<Voluntario, VoluntarioId> {
    Optional<Voluntario> findByUsuarioId(UsuarioId usuarioId);

    default Page<Voluntario> findAll(Pageable pageable) {
        return Page.empty();
    }

    Page<Voluntario> findFiltered(String q, Pageable pageable);
}
