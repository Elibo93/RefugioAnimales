package es.refugio.animales.refugio.infraestructure.db.jpa.repository.persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.refugio.animales.refugio.infraestructure.db.jpa.entity.PersonaEntity;

@Repository
public interface PersonaEntityJpaRepository extends JpaRepository<PersonaEntity, Integer> {

    public PersonaEntity findByNombre(String nombre);

}
















