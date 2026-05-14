package es.refugio.refugio.infraestructure.db.jpa.repository.tarea;

import java.util.List;
import es.refugio.refugio.domain.model.tarea.TareaHistorial;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaHistorialRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaHistorialEntity;
import es.refugio.refugio.infraestructure.mapper.TareaHistorialMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TareaHistorialJpaRepositoryImpl implements TareaHistorialRepository {

    private final TareaHistorialEntityJpaRepository jpaRepository;

    @Override
    public TareaHistorial save(TareaHistorial historial) {
        TareaHistorialEntity entity = TareaHistorialMapper.toEntity(historial);
        TareaHistorialEntity saved = jpaRepository.save(entity);
        return TareaHistorialMapper.toDomain(saved);
    }

    @Override
    public List<TareaHistorial> findByTareaId(TareaId tareaId) {
        List<TareaHistorialEntity> entities = jpaRepository.findByTareaIdOrderByFechaCambioDesc(tareaId.getValue());
        return TareaHistorialMapper.toDomain(entities);
    }
}
