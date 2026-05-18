package es.refugio.refugio.domain.repository;

import java.util.Optional;
import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdoptanteRepository extends CRUDRepository<Adoptante, AdoptanteId> {

    Optional<Adoptante> getByUsuarioId(UsuarioId usuarioId);

    default Page<Adoptante> findAll(Pageable pageable) {
        return Page.empty();
    }

    Page<Adoptante> findFiltered(String q, Pageable pageable);
}