package es.refugio.refugio.infraestructure.db.jpa.repository.gamificacion;

import es.refugio.refugio.domain.model.gamificacion.UsuarioLogro;
import es.refugio.refugio.domain.repository.gamificacion.UsuarioLogroRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion.UsuarioLogroEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion.UsuarioLogroId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UsuarioLogroJpaRepositoryImpl implements UsuarioLogroRepository {
    private final JpaUsuarioLogroRepository jpaRepository;

    public UsuarioLogroJpaRepositoryImpl(JpaUsuarioLogroRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<UsuarioLogro> findByUsuarioId(Long usuarioId) {
        return jpaRepository.findAll().stream()
                .filter(e -> e.getId().getUsuarioId().equals(usuarioId))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(UsuarioLogro usuarioLogro) {
        UsuarioLogroId id = new UsuarioLogroId(usuarioLogro.getUsuarioId(), usuarioLogro.getLogroId());
        jpaRepository.save(new UsuarioLogroEntity(id, usuarioLogro.getFechaDesbloqueo()));
    }

    @Override
    public boolean existsByUsuarioIdAndLogroId(Long usuarioId, Long logroId) {
        UsuarioLogroId id = new UsuarioLogroId(usuarioId, logroId);
        return jpaRepository.existsById(id);
    }

    private UsuarioLogro toDomain(UsuarioLogroEntity entity) {
        return new UsuarioLogro(entity.getId().getUsuarioId(), entity.getId().getLogroId(), 
                entity.getFechaDesbloqueo());
    }
}
