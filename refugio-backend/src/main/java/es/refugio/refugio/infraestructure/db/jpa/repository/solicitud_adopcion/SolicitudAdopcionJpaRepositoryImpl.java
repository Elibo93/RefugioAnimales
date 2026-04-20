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

@RequiredArgsConstructor
public class SolicitudAdopcionJpaRepositoryImpl implements SolicitudAdopcionRepository {

    private final SolicitudAdopcionEntityJpaRepository jpaRepository;

    @Override
    public SolicitudAdopcion save(SolicitudAdopcion solicitudAdopcion) {
        SolicitudAdopcionEntity entity = SolicitudAdopcionMapper.toEntity(solicitudAdopcion);
        SolicitudAdopcionEntity savedEntity = jpaRepository.save(entity);
        return SolicitudAdopcionMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<SolicitudAdopcion> getById(SolicitudAdopcionId id) {
        return jpaRepository.findById(id.getValue())
                .map(SolicitudAdopcionMapper::toDomain);
    }

    @Override
    public List<SolicitudAdopcion> getAll() {
        return SolicitudAdopcionMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public void deleteById(SolicitudAdopcionId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<SolicitudAdopcion> getByAnimalId(AnimalId animalId) {
        List<SolicitudAdopcionEntity> entities = jpaRepository.findByAnimalId(animalId.getValue());
        return SolicitudAdopcionMapper.toDomain(entities);
    }

    @Override
    public List<SolicitudAdopcion> getByAdoptanteId(AdoptanteId adoptanteId) {
        List<SolicitudAdopcionEntity> entities = jpaRepository.findByAdoptanteId(adoptanteId.getValue());
        return SolicitudAdopcionMapper.toDomain(entities);
    }
}
