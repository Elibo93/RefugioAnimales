package es.refugio.refugio.infraestructure.db.jpa.repository.voluntario;

import java.util.Optional;
import java.util.List;

import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import es.refugio.refugio.infraestructure.mapper.VoluntarioMapper;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class VoluntarioJpaRepositoryImpl implements VoluntarioRepository {

    private final VoluntarioEntityJpaRepository repository;

    @Override
    public Voluntario save(Voluntario voluntario) {
        VoluntarioEntity entity = VoluntarioMapper.toEntity(voluntario);
        return VoluntarioMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Voluntario> getAll() {
        return VoluntarioMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Voluntario> getById(VoluntarioId id) {
        return repository.findById(id.getValue()).map(VoluntarioMapper::toDomain);
    }

    @Override
    public void deleteById(VoluntarioId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public Optional<Voluntario> findByUsuarioId(UsuarioId usuarioId) {
        return repository.findByUsuarioId(usuarioId.getValue()).map(VoluntarioMapper::toDomain);
    }

    @Override
    public Page<Voluntario> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(VoluntarioMapper::toDomain);
    }

    @Override
    public Page<Voluntario> findFiltered(String q, Pageable pageable) {
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
        }, pageable).map(VoluntarioMapper::toDomain);
    }
}
