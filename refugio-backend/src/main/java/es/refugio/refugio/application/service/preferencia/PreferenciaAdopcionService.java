package es.refugio.refugio.application.service.preferencia;

import es.refugio.refugio.domain.model.preferencia.PreferenciaAdopcion;
import es.refugio.refugio.infraestructure.db.jpa.entity.PreferenciaAdopcionEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.preferencia.JpaPreferenciaAdopcionRepository;
import es.refugio.refugio.infraestructure.mapper.PreferenciaAdopcionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Preferencia Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class PreferenciaAdopcionService {

    private final JpaPreferenciaAdopcionRepository repository;
    private final PreferenciaAdopcionMapper mapper;

    @Transactional(readOnly = true)
    public Optional<PreferenciaAdopcion> findByUsuarioId(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId).map(mapper::toDomain);
    }

    @Transactional
    public PreferenciaAdopcion save(PreferenciaAdopcion preferencia) {
        Optional<PreferenciaAdopcionEntity> existing = repository.findByUsuarioId(preferencia.getUsuarioId());
        
        PreferenciaAdopcionEntity entity = mapper.toEntity(preferencia);
        
        if (existing.isPresent()) {
            entity.setId(existing.get().getId());
            entity.setCreatedAt(existing.get().getCreatedAt());
            entity.setUpdatedAt(LocalDateTime.now());
        } else {
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
        }
        
        return mapper.toDomain(repository.save(entity));
    }
}
