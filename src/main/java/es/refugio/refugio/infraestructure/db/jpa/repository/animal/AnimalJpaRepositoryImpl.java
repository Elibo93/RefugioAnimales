package es.refugio.refugio.infraestructure.db.jpa.repository.animal;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.mapper.AnimalMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnimalJpaRepositoryImpl implements AnimalRepository {

    // Atributos
    private final AnimalEntityJpaRepository repository;

    // Implementacion de metodos
    @Override
    public Animal save(Animal t) {
        AnimalEntity animalEntity = AnimalMapper.toEntity(t);
        return AnimalMapper.toDomain(repository.save(animalEntity));
    }

    @Override
    public List<Animal> getAll() {
        return AnimalMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Animal> getById(AnimalId id) {
        Optional<AnimalEntity> te = repository.findById(id.getValue());

        if (te.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(AnimalMapper.toDomain(te.get()));
    }

    @Override
    public void deleteById(AnimalId id) {
        repository.deleteById(id.getValue());
    }
}
