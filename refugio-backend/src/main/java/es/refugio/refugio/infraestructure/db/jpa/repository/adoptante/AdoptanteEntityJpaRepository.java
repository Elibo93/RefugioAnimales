package es.refugio.refugio.infraestructure.db.jpa.repository.adoptante;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;

@Repository
public interface AdoptanteEntityJpaRepository extends JpaRepository<AdoptanteEntity, Integer> {


    // Buscar el perfil de adoptante asociado a un ID de usuario
    Optional<AdoptanteEntity> findByUsuarioId(Integer usuarioId);
    
    // Buscar todos los adoptantes que están esperando validación
    java.util.List<AdoptanteEntity> findByEstadoValidacion(String estado);
}