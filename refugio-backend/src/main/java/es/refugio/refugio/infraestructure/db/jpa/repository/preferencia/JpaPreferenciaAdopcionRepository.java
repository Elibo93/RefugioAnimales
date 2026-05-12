package es.refugio.refugio.infraestructure.db.jpa.repository.preferencia;

import es.refugio.refugio.infraestructure.db.jpa.entity.PreferenciaAdopcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPreferenciaAdopcionRepository extends JpaRepository<PreferenciaAdopcionEntity, Integer> {
    Optional<PreferenciaAdopcionEntity> findByUsuarioId(Integer usuarioId);
    List<PreferenciaAdopcionEntity> findByNotificacionesActivasTrue();
}
