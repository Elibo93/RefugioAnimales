package es.refugio.refugio.infraestructure.db.jpa.repository;

import es.refugio.refugio.infraestructure.db.jpa.entity.NotificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Integer> {
    
    List<NotificacionEntity> findByUsuarioIdOrderByFechaDesc(Integer usuarioId);
    
    @org.springframework.data.jpa.repository.Query("SELECT n FROM NotificacionEntity n WHERE (n.usuarioId = :usuarioId OR n.rol IN :roles) ORDER BY n.fecha DESC")
    List<NotificacionEntity> findByUsuarioIdOrRoles(@org.springframework.data.repository.query.Param("usuarioId") Integer usuarioId, @org.springframework.data.repository.query.Param("roles") java.util.Collection<String> roles);
    
    long countByUsuarioIdAndLeidaFalse(Integer usuarioId);
    
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(n) FROM NotificacionEntity n WHERE (n.usuarioId = :usuarioId OR n.rol IN :roles) AND n.leida = false")
    long countByUsuarioIdOrRolesAndNoLeidas(@org.springframework.data.repository.query.Param("usuarioId") Integer usuarioId, @org.springframework.data.repository.query.Param("roles") java.util.Collection<String> roles);
    
    List<NotificacionEntity> findByUsuarioIdAndLeidaFalseOrderByFechaDesc(Integer usuarioId);
}
