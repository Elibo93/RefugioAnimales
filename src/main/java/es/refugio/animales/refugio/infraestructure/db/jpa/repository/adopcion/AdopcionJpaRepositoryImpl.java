package es.refugio.animales.refugio.infraestructure.db.jpa.repository.adopcion;

import java.util.List;
import java.util.Optional;

import es.refugio.animales.refugio.domain.model.adopcion.Adopcion;
import es.refugio.animales.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.animales.refugio.domain.repository.AdopcionRepository;
import es.refugio.animales.refugio.infraestructure.db.jpa.entity.AdopcionEntity;
import es.refugio.animales.refugio.infraestructure.mapper.AdopcionMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AdopcionJpaRepositoryImpl implements AdopcionRepository {

    private final AdopcionEntityJpaRepository repository;

    @Override
    public Adopcion save(Adopcion i) {
        AdopcionEntity entity = AdopcionMapper.toEntity(i);
        return AdopcionMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Adopcion> getAll() {
        return AdopcionMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Adopcion> getById(AdopcionId id) {
        Optional<AdopcionEntity> entity = repository.findById(id.getValue());

        if (entity.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(AdopcionMapper.toDomain(entity.get()));
    }

    @Override
    public void deleteById(AdopcionId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public List<Adopcion> getByPersonaId(Integer PersonaId) {
        return AdopcionMapper.toDomain(repository.findByPersonaId(PersonaId));
    }

    @Override
    public List<Adopcion> getByAnimalId(Integer AnimalId) {
        return AdopcionMapper.toDomain(repository.findByAnimalId(AnimalId));
    }

}
