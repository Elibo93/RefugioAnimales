package es.refugio.refugio.infraestructure.db.jpa.repository;

import es.refugio.refugio.infraestructure.db.jpa.entity.NotificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Integer> {
    
    List<NotificacionEntity> findByUsuarioIdOrderByFechaDesc(Integer usuarioId);
    
    long countByUsuarioIdAndLeidaFalse(Integer usuarioId);
    
    List<NotificacionEntity> findByUsuarioIdAndLeidaFalseOrderByFechaDesc(Integer usuarioId);
}
