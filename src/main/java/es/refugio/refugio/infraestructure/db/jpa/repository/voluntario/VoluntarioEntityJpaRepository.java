package es.refugio.refugio.infraestructure.db.jpa.repository.voluntario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;

@Repository
public interface VoluntarioEntityJpaRepository extends JpaRepository<VoluntarioEntity, Integer> {
    Optional<VoluntarioEntity> findByUsuarioId(Integer usuarioId);
}
