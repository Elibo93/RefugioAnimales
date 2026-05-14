package es.refugio.refugio.infraestructure.db.jpa.repository.gamificacion;

import es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion.UsuarioLogroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUsuarioLogroRepository extends JpaRepository<UsuarioLogroEntity, Object> {
}
