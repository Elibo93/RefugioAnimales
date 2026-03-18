package es.refugio.refugio.domain.repository;

import java.util.List;

import es.refugio.common.domain.repository.CRUDRepository;
import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;

public interface DonacionRepository extends CRUDRepository<Donacion, DonacionId> {

    List<Donacion> getByUsuarioId(UsuarioId usuarioId);

}
