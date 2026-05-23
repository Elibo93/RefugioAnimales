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
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.refugio.refugio.infraestructure.db.jpa.entity.TareaEntity;
import es.refugio.refugio.infraestructure.db.jpa.entity.DisponibilidadVoluntarioEntity;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoDisponibilidad;

@RequiredArgsConstructor
public class VoluntarioJpaRepositoryImpl implements VoluntarioRepository {

    private final VoluntarioEntityJpaRepository repository;
    private final VoluntarioMapper voluntarioMapper;

    @Override
    public Voluntario save(Voluntario voluntario) {
        if (voluntario.getId() != null) {
            Optional<VoluntarioEntity> existingOpt = repository.findById(voluntario.getId().getValue());
            if (existingOpt.isPresent()) {
                VoluntarioEntity existing = existingOpt.get();
                VoluntarioEntity mapped = voluntarioMapper.toEntity(voluntario);
                
                existing.setDisponibilidad(mapped.getDisponibilidad());
                existing.setEspecialidad(mapped.getEspecialidad());
                existing.setStatus(mapped.getStatus());
                existing.setTareas(mapped.getTareas());
                
                if (existing.getDisponibilidades() == null) {
                    existing.setDisponibilidades(new ArrayList<>());
                }
                
                List<DisponibilidadVoluntarioEntity> currentList = existing.getDisponibilidades();
                List<DisponibilidadVoluntarioEntity> newList = mapped.getDisponibilidades();
                if (newList == null) newList = new ArrayList<>();
                
                final List<DisponibilidadVoluntarioEntity> finalList = newList;
                currentList.removeIf(curr -> finalList.stream().noneMatch(n -> 
                    n.getId() != null && n.getId().equals(curr.getId())
                ));
                
                for (DisponibilidadVoluntarioEntity newItem : finalList) {
                    if (newItem.getId() != null) {
                        currentList.stream().filter(c -> c.getId().equals(newItem.getId())).findFirst().ifPresent(c -> {
                            c.setFecha(newItem.getFecha());
                            c.setTurno(newItem.getTurno());
                            c.setEstado(newItem.getEstado());
                        });
                    } else {
                        newItem.setVoluntario(existing);
                        currentList.add(newItem);
                    }
                }
                
                return voluntarioMapper.toDomain(repository.save(existing));
            }
        }
        
        VoluntarioEntity entity = voluntarioMapper.toEntity(voluntario);
        if (entity.getDisponibilidades() != null) {
            entity.getDisponibilidades().forEach(d -> d.setVoluntario(entity));
        }
        return voluntarioMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Voluntario> getAll() {
        return voluntarioMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Voluntario> getById(VoluntarioId id) {
        return repository.findById(id.getValue()).map(voluntarioMapper::toDomain);
    }

    @Override
    public void deleteById(VoluntarioId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public Optional<Voluntario> findByUsuarioId(UsuarioId usuarioId) {
        return repository.findByUsuarioId(usuarioId.getValue()).map(voluntarioMapper::toDomain);
    }

    @Override
    public Page<Voluntario> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(voluntarioMapper::toDomain);
    }

    @Override
    public Page<Voluntario> findFiltered(String q, Integer excludeTareaId, String excludeDate, Pageable pageable) {
        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.trim().isEmpty()) {
                String pattern = "%" + q.toLowerCase().trim() + "%";
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<PerfilLegalEntity> perfilRoot = subquery.from(PerfilLegalEntity.class);
                subquery.select(perfilRoot.get("usuarioId"));
                subquery.where(cb.or(
                    cb.like(cb.lower(perfilRoot.get("nombre")), pattern),
                    cb.like(cb.lower(perfilRoot.get("apellido")), pattern),
                    cb.like(cb.lower(perfilRoot.get("dni")), pattern)
                ));
                predicates.add(root.get("usuarioId").in(subquery));
            }

            if (excludeTareaId != null) {
                Subquery<Integer> subT = query.subquery(Integer.class);
                Root<TareaEntity> rootT = subT.from(TareaEntity.class);
                Join<TareaEntity, VoluntarioEntity> joinV = rootT.join("voluntarios");
                subT.select(joinV.get("id")).where(cb.equal(rootT.get("id"), excludeTareaId));
                predicates.add(cb.not(root.get("id").in(subT)));
            }

            if (excludeDate != null && !excludeDate.isEmpty()) {
                Subquery<Integer> subD = query.subquery(Integer.class);
                Root<DisponibilidadVoluntarioEntity> rootD = subD.from(DisponibilidadVoluntarioEntity.class);
                subD.select(rootD.get("voluntario").get("id"));
                subD.where(
                    cb.equal(rootD.get("fecha"), java.time.LocalDate.parse(excludeDate)),
                    cb.equal(rootD.get("estado"), EstadoDisponibilidad.NO_DISPONIBLE)
                );
                predicates.add(cb.not(root.get("id").in(subD)));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable).map(voluntarioMapper::toDomain);
    }
}
