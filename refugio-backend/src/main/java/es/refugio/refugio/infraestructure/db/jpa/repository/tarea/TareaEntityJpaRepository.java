package es.refugio.refugio.infraestructure.db.jpa.repository.tarea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;

import java.time.LocalDateTime;
import java.util.List;
import es.refugio.refugio.domain.model.tarea.enums.EstadoTarea;

@Repository
public interface TareaEntityJpaRepository extends JpaRepository<TareaEntity, Integer> {
    List<TareaEntity> findByFechaLimiteBetweenAndNotificadoVencimientoFalseAndEstadoIn(
        LocalDateTime start, LocalDateTime end, List<EstadoTarea> estados);
}
