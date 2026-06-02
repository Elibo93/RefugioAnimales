package es.refugio.refugio.infraestructure.db.jpa.repository.gamificacion;

import es.refugio.refugio.domain.model.gamificacion.UsuarioMetricas;
import es.refugio.refugio.domain.repository.gamificacion.UsuarioMetricasRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.gamificacion.UsuarioMetricasEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioMetricasJpaRepositoryImpl implements UsuarioMetricasRepository {
    private final JpaUsuarioMetricasRepository jpaRepository;

    public UsuarioMetricasJpaRepositoryImpl(JpaUsuarioMetricasRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UsuarioMetricas> findByUsuarioId(Long usuarioId) {
        return jpaRepository.findById(usuarioId).map(this::toDomain);
    }

    @Override
    public void save(UsuarioMetricas metricas) {
        jpaRepository.save(toEntity(metricas));
    }

    private UsuarioMetricas toDomain(UsuarioMetricasEntity entity) {
        return new UsuarioMetricas(entity.getUsuarioId(), entity.getTareasCompletadas(), 
                entity.getTotalDonado(), entity.getFechaPrimerAporte(), entity.getUltimaActualizacion());
    }

    private UsuarioMetricasEntity toEntity(UsuarioMetricas metricas) {
        return new UsuarioMetricasEntity(metricas.getUsuarioId(), metricas.getTareasCompletadas(), 
                metricas.getTotalDonado(), metricas.getFechaPrimerAporte(), metricas.getUltimaActualizacion());
    }
}
