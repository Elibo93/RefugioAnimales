package es.refugio.refugio.infraestructure.db.jpa.repository;

import es.refugio.refugio.infraestructure.db.jpa.entity.FavoritoAnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoAnimalJpaRepository extends JpaRepository<FavoritoAnimalEntity, Integer> {
    
    Optional<FavoritoAnimalEntity> findByUsuarioIdAndAnimalId(Integer usuarioId, Integer animalId);
    
    List<FavoritoAnimalEntity> findByUsuarioId(Integer usuarioId);
    
    int countByAnimalId(Integer animalId);
}
