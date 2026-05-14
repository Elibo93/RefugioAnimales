package es.refugio.refugio.domain.repository.gamificacion;

import es.refugio.refugio.domain.model.gamificacion.UsuarioMetricas;
import java.util.Optional;

public interface UsuarioMetricasRepository {
    Optional<UsuarioMetricas> findByUsuarioId(Long usuarioId);
    void save(UsuarioMetricas metricas);
}
