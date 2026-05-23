package es.refugio.refugio.infraestructure.db.jpa.repository.tarea;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;
import es.refugio.refugio.infraestructure.mapper.TareaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class TareaJpaRepositoryImpl implements TareaRepository {

    private final TareaEntityJpaRepository jpaRepository;
    private final TareaMapper tareaMapper;

    @Override
    public Tarea save(Tarea tarea) {
        TareaEntity entity = tareaMapper.toEntity(tarea);
        return tareaMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Tarea> getById(TareaId id) {
        return jpaRepository.findById(id.getValue())
                .map(tareaMapper::toDomain);
    }

    @Override
    public List<Tarea> getAll() {
        return tareaMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public Page<Tarea> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(tareaMapper::toDomain);
    }

    @Override
    public void deleteById(TareaId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
