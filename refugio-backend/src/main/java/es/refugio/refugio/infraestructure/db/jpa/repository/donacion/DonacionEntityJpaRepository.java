package es.refugio.refugio.infraestructure.db.jpa.repository.donacion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.infraestructure.db.jpa.entity.DonacionEntity;

@Repository
public interface DonacionEntityJpaRepository extends JpaRepository<DonacionEntity, Integer> {
    List<DonacionEntity> findByUsuarioId(Integer usuarioId);
}
