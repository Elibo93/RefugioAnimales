package es.refugio.refugio.infraestructure.db.jpa.repository.perfil_legal;

import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PerfilLegalEntityJpaRepository extends JpaRepository<PerfilLegalEntity, Integer> {
    Optional<PerfilLegalEntity> findByUsuarioId(Integer usuarioId);
    Optional<PerfilLegalEntity> findByDni(String dni);
}
