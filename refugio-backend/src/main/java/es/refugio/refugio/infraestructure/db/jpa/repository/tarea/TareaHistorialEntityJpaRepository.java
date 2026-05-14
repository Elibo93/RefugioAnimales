package es.refugio.refugio.infraestructure.db.jpa.repository.tarea;

import java.util.List;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaHistorialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaHistorialEntityJpaRepository extends JpaRepository<TareaHistorialEntity, Integer> {
    List<TareaHistorialEntity> findByTareaIdOrderByFechaCambioDesc(Integer tareaId);
}
