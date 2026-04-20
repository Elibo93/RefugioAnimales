package es.refugio.refugio.infraestructure.db.jpa.repository.solicitud_adopcion;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.infraestructure.db.jpa.entity.SolicitudAdopcionEntity;

@Repository
public interface SolicitudAdopcionEntityJpaRepository extends JpaRepository<SolicitudAdopcionEntity, Integer> {
    List<SolicitudAdopcionEntity> findByAnimalId(Integer animalId);
    List<SolicitudAdopcionEntity> findByAdoptanteId(Integer adoptanteId);
}
