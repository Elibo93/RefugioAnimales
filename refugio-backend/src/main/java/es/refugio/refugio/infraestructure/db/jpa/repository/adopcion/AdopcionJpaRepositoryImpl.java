package es.refugio.refugio.infraestructure.db.jpa.repository.adopcion;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdopcionEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import es.refugio.refugio.infraestructure.mapper.AdopcionMapper;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AdopcionJpaRepositoryImpl implements AdopcionRepository {

    private final AdopcionEntityJpaRepository repository;

    @Override
    public Adopcion save(Adopcion t) {
        AdopcionEntity entity = AdopcionMapper.toEntity(t);
        return AdopcionMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Adopcion> getAll() {
        return AdopcionMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Adopcion> getById(AdopcionId id) {
        return repository.findById(id.getValue()).map(AdopcionMapper::toDomain);
    }

    @Override
    public void deleteById(AdopcionId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public List<Adopcion> getByAdoptanteId(AdoptanteId adoptanteId) {
        return AdopcionMapper.toDomain(repository.findByAdoptanteId(adoptanteId.getValue()));
    }

    @Override
    public List<Adopcion> getByAnimalId(AnimalId animalId) {
        return AdopcionMapper.toDomain(repository.findByAnimalId(animalId.getValue()));
    }

    @Override
    public Optional<Adopcion> getByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId) {
        return repository.findByAdoptanteIdAndAnimalId(adoptanteId.getValue(), animalId.getValue())
                .map(AdopcionMapper::toDomain);
    }

    @Override
    public boolean existsByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId) {
        return repository.existsByAdoptanteIdAndAnimalId(adoptanteId.getValue(), animalId.getValue());
    }

    @Override
    public List<Adopcion> findByCriteria(AdoptanteId adoptanteId, AnimalId animalId) {
        return AdopcionMapper.toDomain(repository.findByAdoptanteIdAndAnimalId(adoptanteId.getValue(), animalId.getValue())
                .map(List::of).orElse(List.of()));
    }

    @Override
    public boolean existsByAnimalId(AnimalId animalId) {
        return repository.existsByAnimalId(animalId.getValue());
    }

    @Override
    public Page<Adopcion> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(AdopcionMapper::toDomain);
    }

    @Override
    public Page<Adopcion> findFiltered(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            return findAll(pageable);
        }

        return repository.findAll((root, query, cb) -> {
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

            return cb.or(animalNombreLike, adoptanteNombreLike);
        }, pageable).map(AdopcionMapper::toDomain);
    }
}