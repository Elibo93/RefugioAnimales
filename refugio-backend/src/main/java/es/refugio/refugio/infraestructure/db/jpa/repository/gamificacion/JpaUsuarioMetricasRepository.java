package es.refugio.refugio.infraestructure.db.jpa.repository.gamificacion;

import es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion.UsuarioMetricasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUsuarioMetricasRepository extends JpaRepository<UsuarioMetricasEntity, Long> {
}
