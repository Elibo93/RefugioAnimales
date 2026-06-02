package es.refugio.refugio.infraestructure.db.jpa.repository.adoptante;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.adoptante.Adoptante;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.AdoptanteRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.AdoptanteEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import es.refugio.refugio.infraestructure.mapper.AdoptanteMapper;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class AdoptanteJpaRepositoryImpl implements AdoptanteRepository {

    private final AdoptanteEntityJpaRepository repository;
    private final AdoptanteMapper adoptanteMapper;

    @Override
    public Adoptante save(Adoptante t) {
        AdoptanteEntity adoptanteEntity = adoptanteMapper.toEntity(t);
        return adoptanteMapper.toDomain(repository.save(adoptanteEntity));
    }

    @Override
    public List<Adoptante> getAll() {
        return adoptanteMapper.toDomain(repository.findAll());
    }

    @Override
    public Page<Adoptante> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(adoptanteMapper::toDomain);
    }

    @Override
    public Optional<Adoptante> getById(AdoptanteId id) {
        return repository.findById(id.getValue())
                .map(adoptanteMapper::toDomain);
    }

    @Override
    public void deleteById(AdoptanteId id) {
        repository.deleteById(id.getValue());
    }


    @Override
    public Optional<Adoptante> getByUsuarioId(UsuarioId usuarioId) {
        return repository.findByUsuarioId(usuarioId.getValue())
                .map(adoptanteMapper::toDomain);
    }

    @Override
    public Page<Adoptante> findFiltered(String q, Pageable pageable) {
        if (q == null || q.trim().isEmpty()) {
            return findAll(pageable);
        }

        return repository.findAll((root, query, cb) -> {
            String pattern = "%" + q.toLowerCase().trim() + "%";
            
            // Subconsulta para buscar en PerfilLegal por nombre, apellido o DNI
            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<PerfilLegalEntity> perfilRoot = subquery.from(PerfilLegalEntity.class);
            subquery.select(perfilRoot.get("usuarioId"));
            subquery.where(cb.or(
                cb.like(cb.lower(perfilRoot.get("nombre")), pattern),
                cb.like(cb.lower(perfilRoot.get("apellido")), pattern),
                cb.like(cb.lower(perfilRoot.get("dni")), pattern)
            ));

            return root.get("usuarioId").in(subquery);
        }, pageable).map(adoptanteMapper::toDomain);
    }
}