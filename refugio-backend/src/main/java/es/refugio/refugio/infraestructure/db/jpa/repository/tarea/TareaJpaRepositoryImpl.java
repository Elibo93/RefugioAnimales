package es.refugio.refugio.infraestructure.db.jpa.repository.tarea;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.tarea.Tarea;
import es.refugio.refugio.domain.model.tarea.TareaId;
import es.refugio.refugio.domain.repository.TareaRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;
import es.refugio.refugio.infraestructure.mapper.TareaMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TareaJpaRepositoryImpl implements TareaRepository {

    private final TareaEntityJpaRepository jpaRepository;

    @Override
    public Tarea save(Tarea tarea) {
        TareaEntity entity = TareaMapper.toEntity(tarea);
        return TareaMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Tarea> getById(TareaId id) {
        return jpaRepository.findById(id.getValue())
                .map(TareaMapper::toDomain);
    }

    @Override
    public List<Tarea> getAll() {
        return TareaMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public void deleteById(TareaId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
