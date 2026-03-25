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

    @Override
    public List<Animal> findFiltered(String especie, String tamano, java.util.List<String> edad, String sexo, Boolean urgencia) {
        return AnimalMapper.toDomain(repository.findAll((root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (especie != null && !especie.isEmpty() && !"ALL".equalsIgnoreCase(especie)) {
                predicates.add(cb.equal(root.get("especie"), Especie.valueOf(especie.toUpperCase())));
            }
            if (tamano != null && !tamano.isEmpty() && !"ALL".equalsIgnoreCase(tamano)) {
                predicates.add(cb.equal(root.get("tamano"), tamano.toUpperCase()));
            }
            if (sexo != null && !sexo.isEmpty()) {
                predicates.add(cb.equal(root.get("sexo"), sexo.toUpperCase()));
            }
            if (urgencia != null && urgencia) {
                predicates.add(cb.equal(root.get("urgencia"), true));
            }
            
            // Age mapping [cachorro, adulto, senior]
            if (edad != null && !edad.isEmpty()) {
                var agePredicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
                for (String e : edad) {
                    if ("cachorro".equalsIgnoreCase(e)) {
                        agePredicates.add(cb.lessThan(root.get("edad"), 2));
                    } else if ("adulto".equalsIgnoreCase(e)) {
                        agePredicates.add(cb.between(root.get("edad"), 2, 7));
                    } else if ("senior".equalsIgnoreCase(e)) {
                        agePredicates.add(cb.greaterThan(root.get("edad"), 7));
                    }
                }
                if (!agePredicates.isEmpty()) {
                    predicates.add(cb.or(agePredicates.toArray(new jakarta.persistence.criteria.Predicate[0])));
                }
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }));
    }
}