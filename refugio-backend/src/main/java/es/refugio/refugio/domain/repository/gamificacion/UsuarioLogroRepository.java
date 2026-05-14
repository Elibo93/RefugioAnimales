package es.refugio.refugio.domain.repository.gamificacion;

import es.refugio.refugio.domain.model.gamificacion.UsuarioLogro;
import java.util.List;

public interface UsuarioLogroRepository {
    List<UsuarioLogro> findByUsuarioId(Long usuarioId);
    void save(UsuarioLogro usuarioLogro);
    boolean existsByUsuarioIdAndLogroId(Long usuarioId, Long logroId);
}
