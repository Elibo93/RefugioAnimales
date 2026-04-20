package es.refugio.refugio.infraestructure.db.jpa.repository.historial_medico;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.infraestructure.db.jpa.entity.HistorialMedicoEntity;

@Repository
public interface HistorialMedicoEntityJpaRepository extends JpaRepository<HistorialMedicoEntity, Integer> {
    List<HistorialMedicoEntity> findByAnimalId(Integer animalId);
}
