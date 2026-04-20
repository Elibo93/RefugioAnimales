package es.refugio.refugio.infraestructure.db.jpa.repository.tarea;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;

@Repository
public interface TareaEntityJpaRepository extends JpaRepository<TareaEntity, Integer> {
}
