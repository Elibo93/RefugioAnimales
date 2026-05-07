package es.refugio.refugio.infraestructure.db.jpa.repository.donacion;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import es.refugio.refugio.domain.model.donacion.ObjetivoDonacion;
import es.refugio.refugio.domain.model.donacion.ObjetivoDonacionId;
import es.refugio.refugio.domain.repository.ObjetivoDonacionRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.ObjetivoDonacionEntity;
import es.refugio.refugio.infraestructure.mapper.ObjetivoDonacionMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ObjetivoDonacionJpaRepositoryImpl implements ObjetivoDonacionRepository {

    private final ObjetivoDonacionEntityJpaRepository jpaRepository;

    @Override
    public ObjetivoDonacion save(ObjetivoDonacion objetivo) {
        ObjetivoDonacionEntity entity = ObjetivoDonacionMapper.toEntity(objetivo);
        return ObjetivoDonacionMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<ObjetivoDonacion> getById(ObjetivoDonacionId id) {
        return jpaRepository.findById(id.getValue())
                .map(ObjetivoDonacionMapper::toDomain);
    }

    @Override
    public List<ObjetivoDonacion> getAll() {
        return jpaRepository.findAll().stream()
                .map(ObjetivoDonacionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(ObjetivoDonacionId id) {
        jpaRepository.deleteById(id.getValue());
    }
}
