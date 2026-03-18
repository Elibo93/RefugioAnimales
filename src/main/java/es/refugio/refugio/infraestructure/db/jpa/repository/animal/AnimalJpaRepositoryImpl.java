package es.refugio.refugio.infraestructure.db.jpa.repository.animal;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.repository.AnimalRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AnimalEntity;
import es.refugio.refugio.infraestructure.mapper.AnimalMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnimalJpaRepositoryImpl implements AnimalRepository {

    private final AnimalEntityJpaRepository repository;

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
        return repository.findById(id.getValue())
                .map(AnimalMapper::toDomain);
    }

    @Override
    public void deleteById(AnimalId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public Optional<Animal> getByChipId(String chipId) {
        return repository.findByChipId(chipId)
                .map(AnimalMapper::toDomain);
    }

    @Override
    public List<Animal> getByEstado(EstadoAnimal estado) {
        return AnimalMapper.toDomain(repository.findByEstado(estado));
    }

    @Override
    public List<Animal> getByEspecie(Especie especie) {
        return AnimalMapper.toDomain(repository.findByEspecie(especie));
    }
}