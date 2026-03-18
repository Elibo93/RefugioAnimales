package es.refugio.refugio.infraestructure.db.jpa.repository.usuario;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;

@Repository
public interface UsuarioEntityJpaRepository extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByEmail(String email);

}