package es.refugio.animales.refugio.infraestructure.db.jpa.repository.voluntario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.refugio.animales.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;

@Repository
public interface VoluntarioEntityJpaRepository extends JpaRepository<VoluntarioEntity, Integer> {
    
    public VoluntarioEntity findByNombre(String nombre);

}
















