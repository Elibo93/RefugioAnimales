package es.refugio.refugio.infraestructure.db.jpa.repository;

import es.refugio.refugio.infraestructure.db.jpa.entity.NotificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Integer> {
    
    List<NotificacionEntity> findByUsuarioIdOrderByFechaDesc(Integer usuarioId);
    
    @Query("SELECT n FROM NotificacionEntity n WHERE (n.usuarioId = :usuarioId OR n.rol IN :roles) ORDER BY n.fecha DESC")
    List<NotificacionEntity> findByUsuarioIdOrRoles(@Param("usuarioId") Integer usuarioId, @Param("roles") Collection<String> roles);
    
    long countByUsuarioIdAndLeidaFalse(Integer usuarioId);

    @Query("SELECT COUNT(n) FROM NotificacionEntity n WHERE (n.usuarioId = :usuarioId OR n.rol IN :roles) AND n.leida = false")
    long countByUsuarioIdOrRolesAndNoLeidas(@Param("usuarioId") Integer usuarioId, @Param("roles") Collection<String> roles);
    
    List<NotificacionEntity> findByUsuarioIdAndLeidaFalseOrderByFechaDesc(Integer usuarioId);
}
