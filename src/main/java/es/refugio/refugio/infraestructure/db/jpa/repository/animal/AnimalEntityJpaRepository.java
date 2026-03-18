package es.refugio.refugio.infraestructure.db.jpa.repository.animal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;

@Repository
public interface AnimalEntityJpaRepository extends JpaRepository<AnimalEntity, Integer> {

    public AnimalEntity findByNombre(String nombre);
}
















