package es.refugio.refugio.infraestructure.db.jpa.repository.adopcion;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdopcionEntity;

@Repository
public interface AdopcionEntityJpaRepository extends JpaRepository<AdopcionEntity, Integer> {

    List<AdopcionEntity> findByAnimalId(Integer animalId);

    List<AdopcionEntity> findByAdoptanteId(Integer adoptanteId);

    Optional<AdopcionEntity> findByAdoptanteIdAndAnimalId(Integer adoptanteId, Integer animalId);

    boolean existsByAdoptanteIdAndAnimalId(Integer adoptanteId, Integer animalId);
}