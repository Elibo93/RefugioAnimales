package es.refugio.refugio.domain.repository;

import java.util.List;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DonacionRepository extends CRUDRepository<Donacion, DonacionId> {

    List<Donacion> getByUsuarioId(UsuarioId usuarioId);

    default Page<Donacion> findAll(Pageable pageable) {
        return Page.empty();
    }
}
