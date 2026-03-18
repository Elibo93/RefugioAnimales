package es.refugio.refugio.infraestructure.db.jpa.repository.voluntario;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.mapper.VoluntarioMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VoluntarioJpaRepositoryImpl implements VoluntarioRepository {

    // Atributos
    private final VoluntarioEntityJpaRepository repository;

    // Implementacion de metodos
    @Override
    public Voluntario save(Voluntario t) {
        VoluntarioEntity voluntarioEntity = VoluntarioMapper.toEntity(t);
        return VoluntarioMapper.toDomain(repository.save(voluntarioEntity));
    }

    @Override
    public List<Voluntario> getAll() {
        return VoluntarioMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Voluntario> getById(VoluntarioId id) {
        Optional<VoluntarioEntity> entity = repository.findById(id.getValue());

        if (entity.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(VoluntarioMapper.toDomain(entity.get()));
    }

    @Override
    public void deleteById(VoluntarioId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public Optional<Voluntario> getByName(String name) {
        VoluntarioEntity Voluntario = repository.findByNombre(name);
        if (Voluntario != null)
            return Optional.of(VoluntarioMapper.toDomain(Voluntario));
        else
            return Optional.empty();
    }
    // Hereda automáticamente métodos como: save(), findById(), findAll(), delete(),
    // etc.

}
