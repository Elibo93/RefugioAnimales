package es.refugio.refugio.infraestructure.db.jpa.repository.animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
import es.refugio.refugio.domain.model.animal.enums.Sexo;
import es.refugio.refugio.domain.model.animal.enums.Tamano;
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
    public Page<Animal> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(AnimalMapper::toDomain);
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
    public List<Animal> findFiltered(String q, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia) {
        return AnimalMapper.toDomain(repository.findAll(createSpecification(q, null, especie, tamano, edad, sexo, urgencia)));
    }

    @Override
    public Page<Animal> findFiltered(String q, String estado, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia, Pageable pageable) {
        return repository.findAll(createSpecification(q, estado, especie, tamano, edad, sexo, urgencia), pageable).map(AnimalMapper::toDomain);
    }

    private Specification<AnimalEntity> createSpecification(String q, String estado, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (q != null && !q.trim().isEmpty()) {
                String pattern = "%" + q.toLowerCase().trim() + "%";
                System.out.println("DEBUG: Applying search filter 'q' with pattern: " + pattern);
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("nombre")), pattern),
                    cb.like(cb.lower(root.get("raza")), pattern),
                    cb.like(cb.lower(root.get("chipId")), pattern),
                    cb.like(cb.lower(root.get("descripcion")), pattern)
                ));
            }

            if (estado != null && !estado.isEmpty() && !"ALL".equalsIgnoreCase(estado)) {
                System.out.println("DEBUG: Applying estado filter: " + estado);
                predicates.add(cb.equal(root.get("estado"), EstadoAnimal.valueOf(estado.toUpperCase())));
            }

            if (especie != null && !especie.isEmpty() && !"ALL".equalsIgnoreCase(especie)) {
                System.out.println("DEBUG: Applying especie filter: " + especie);
                predicates.add(cb.equal(root.get("especie"), Especie.valueOf(especie.toUpperCase())));
            }
            
            if (tamano != null && !tamano.isEmpty() && !"ALL".equalsIgnoreCase(tamano)) {
                System.out.println("DEBUG: Applying tamano filter: " + tamano);
                Tamano tEnum = null;
                String tLower = tamano.toLowerCase();
                if (tLower.contains("pequ")) {
                    tEnum = Tamano.PEQUEÑO;
                } else if (tLower.contains("med")) {
                    tEnum = Tamano.MEDIANO;
                } else if (tLower.contains("grand")) {
                    tEnum = Tamano.GRANDE;
                } else if (tLower.contains("gig")) {
                    tEnum = Tamano.GIGANTE;
                } else {
                    try {
                        tEnum = Tamano.valueOf(tamano.toUpperCase());
                    } catch (Exception ignored) {}
                }
                if (tEnum != null) {
                    predicates.add(cb.equal(root.get("tamano"), tEnum));
                }
            }
            
            if (sexo != null && !sexo.isEmpty() && !"ALL".equalsIgnoreCase(sexo)) {
                System.out.println("DEBUG: Applying sexo filter: " + sexo);
                predicates.add(cb.equal(root.get("sexo"), Sexo.valueOf(sexo.toUpperCase())));
            }
            
            if (urgencia != null && urgencia) {
                System.out.println("DEBUG: Applying urgencia filter: true");
                predicates.add(cb.equal(root.get("urgencia"), true));
            }
            
            if (edad != null && !edad.isEmpty()) {
                System.out.println("DEBUG: Applying edad filters: " + edad);
                var agePredicates = new ArrayList<Predicate>();
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
                    predicates.add(cb.or(agePredicates.toArray(new Predicate[0])));
                }
            }

            Predicate finalPredicate = cb.and(predicates.toArray(new Predicate[0]));
            return finalPredicate;
        };
    }

    @Override
    public List<Animal> findTop3ByEstadoOrderByVisitasDesc(EstadoAnimal estado) {
        return AnimalMapper.toDomain(repository.findTop3ByEstadoOrderByVisitasDesc(estado));
    }

    @Override
    public void incrementarVisitas(AnimalId id) {
        repository.incrementarVisitas(id.getValue());
    }
}