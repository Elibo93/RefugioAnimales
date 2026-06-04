package es.refugio.refugio.infraestructure.db.jpa.repository.solicitud_adopcion;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.SolicitudAdopcionEntity;
import es.refugio.refugio.infraestructure.mapper.SolicitudAdopcionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class SolicitudAdopcionJpaRepositoryImpl implements SolicitudAdopcionRepository {

    private final SolicitudAdopcionEntityJpaRepository jpaRepository;
    private final SolicitudAdopcionMapper solicitudAdopcionMapper;

    @Override
    public SolicitudAdopcion save(SolicitudAdopcion solicitudAdopcion) {
        SolicitudAdopcionEntity entity = solicitudAdopcionMapper.toEntity(solicitudAdopcion);
        SolicitudAdopcionEntity savedEntity = jpaRepository.save(entity);
        return solicitudAdopcionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<SolicitudAdopcion> getById(SolicitudAdopcionId id) {
        return jpaRepository.findById(id.getValue())
                .map(solicitudAdopcionMapper::toDomain);
    }

    @Override
    public List<SolicitudAdopcion> getAll() {
        return solicitudAdopcionMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public Page<SolicitudAdopcion> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(solicitudAdopcionMapper::toDomain);
    }

    @Override
    public void deleteById(SolicitudAdopcionId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<SolicitudAdopcion> getByAnimalId(AnimalId animalId) {
        List<SolicitudAdopcionEntity> entities = jpaRepository.findByAnimalId(animalId.getValue());
        return solicitudAdopcionMapper.toDomain(entities);
    }

    @Override
    public List<SolicitudAdopcion> getByAdoptanteId(AdoptanteId adoptanteId) {
        List<SolicitudAdopcionEntity> entities = jpaRepository.findByAdoptanteId(adoptanteId.getValue());
        return solicitudAdopcionMapper.toDomain(entities);
    }
}
