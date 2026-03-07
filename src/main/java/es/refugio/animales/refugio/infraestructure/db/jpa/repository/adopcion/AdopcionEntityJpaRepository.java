package es.refugio.animales.refugio.infraestructure.db.jpa.repository.adopcion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.refugio.animales.refugio.infraestructure.db.jpa.entity.AdopcionEntity;

@Repository
public interface AdopcionEntityJpaRepository extends JpaRepository<AdopcionEntity, Integer> {

    // Buscar adopciones por Persona
    public List<AdopcionEntity> findByPersonaId(Integer idPersona);

    // Buscar adopciones por Animal
    public List<AdopcionEntity> findByAnimalId(Integer idAnimal);

    // // Evitar duplicados (Persona ya inscrito en un Animal)
    // public AdopcionEntity findByIdPersonaAndIdAnimal(Integer idPersona, Integer
    // idAnimal);
}
















