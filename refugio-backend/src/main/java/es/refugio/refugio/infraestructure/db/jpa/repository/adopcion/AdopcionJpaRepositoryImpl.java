package es.refugio.refugio.infraestructure.db.jpa.repository.adopcion;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdopcionEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import es.refugio.refugio.infraestructure.mapper.AdopcionMapper;
import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AdopcionJpaRepositoryImpl implements AdopcionRepository {

    private final AdopcionEntityJpaRepository repository;
    private final AdopcionMapper adopcionMapper;

    @Override
    public Adopcion save(Adopcion t) {
        AdopcionEntity entity = adopcionMapper.toEntity(t);
        return adopcionMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Adopcion> getAll() {
        return adopcionMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Adopcion> getById(AdopcionId id) {
        return repository.findById(id.getValue()).map(adopcionMapper::toDomain);
    }

    @Override
    public void deleteById(AdopcionId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public List<Adopcion> getByAdoptanteId(AdoptanteId adoptanteId) {
        return adopcionMapper.toDomain(repository.findByAdoptanteId(adoptanteId.getValue()));
    }

    @Override
    public List<Adopcion> getByAnimalId(AnimalId animalId) {
        return adopcionMapper.toDomain(repository.findByAnimalId(animalId.getValue()));
    }

    @Override
    public Optional<Adopcion> getByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId) {
        return repository.findByAdoptanteIdAndAnimalId(adoptanteId.getValue(), animalId.getValue())
                .map(adopcionMapper::toDomain);
    }

    @Override
    public boolean existsByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId) {
        return repository.existsByAdoptanteIdAndAnimalId(adoptanteId.getValue(), animalId.getValue());
    }

    @Override
    public List<Adopcion> findByCriteria(AdoptanteId adoptanteId, AnimalId animalId) {
        return adopcionMapper.toDomain(repository.findByAdoptanteIdAndAnimalId(adoptanteId.getValue(), animalId.getValue())
                .map(List::of).orElse(List.of()));
    }

    @Override
    public boolean existsByAnimalId(AnimalId animalId) {
        return repository.existsByAnimalId(animalId.getValue());
    }

    @Override
    public List<Adopcion> findByEstadoAndFechaAdopcionBefore(EstadoAdopcion estado, LocalDateTime date) {
        return repository.findByEstadoAndFechaAdopcionBefore(estado, date).stream()
                .map(adopcionMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Adopcion> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(adopcionMapper::toDomain);
    }

    @Override
    public Page<Adopcion> findFiltered(String q, String estado, Pageable pageable) {
        return repository.findAll((root, query, cb) -> {
            Predicate finalPredicate = cb.conjunction();
            
            if (estado != null && !estado.trim().isEmpty()) {
                try {
                    EstadoAdopcion estadoEnum = 
                        EstadoAdopcion.valueOf(estado.toUpperCase().trim());
                    finalPredicate = cb.and(finalPredicate, cb.equal(root.get("estado"), estadoEnum));
                } catch (IllegalArgumentException e) {
                    // ignore invalid estado
                }
            }

            if (q != null && !q.trim().isEmpty()) {
                String pattern = "%" + q.toLowerCase().trim() + "%";
                
                // 1. Buscar en Animal por nombre
                var animalNombreLike = cb.like(cb.lower(root.get("animal").get("nombre")), pattern);
                
                // 2. Buscar en Adoptante -> PerfilLegal por nombre/apellido/dni
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<PerfilLegalEntity> perfilRoot = subquery.from(PerfilLegalEntity.class);
                subquery.select(perfilRoot.get("usuarioId"));
                subquery.where(cb.or(
                    cb.like(cb.lower(perfilRoot.get("nombre")), pattern),
                    cb.like(cb.lower(perfilRoot.get("apellido")), pattern),
                    cb.like(cb.lower(perfilRoot.get("dni")), pattern)
                ));

                var adoptanteNombreLike = root.get("adoptante").get("usuarioId").in(subquery);

                finalPredicate = cb.and(finalPredicate, cb.or(animalNombreLike, adoptanteNombreLike));
            }

            return finalPredicate;
        }, pageable).map(adopcionMapper::toDomain);
    }
}