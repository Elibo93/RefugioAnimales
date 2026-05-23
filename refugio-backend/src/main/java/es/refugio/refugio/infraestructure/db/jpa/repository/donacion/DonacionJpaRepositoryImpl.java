package es.refugio.refugio.infraestructure.db.jpa.repository.donacion;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.donacion.Donacion;
import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.DonacionRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.DonacionEntity;
import es.refugio.refugio.infraestructure.mapper.DonacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class DonacionJpaRepositoryImpl implements DonacionRepository {

    private final DonacionEntityJpaRepository jpaRepository;
    private final DonacionMapper donacionMapper;

    @Override
    public Donacion save(Donacion donacion) {
        DonacionEntity entity = donacionMapper.toEntity(donacion);
        return donacionMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Donacion> getById(DonacionId id) {
        return jpaRepository.findById(id.getValue())
                .map(donacionMapper::toDomain);
    }

    @Override
    public List<Donacion> getAll() {
        return donacionMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public Page<Donacion> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(donacionMapper::toDomain);
    }

    @Override
    public void deleteById(DonacionId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<Donacion> getByUsuarioId(UsuarioId usuarioId) {
        return donacionMapper.toDomain(jpaRepository.findByUsuarioId(usuarioId.getValue()));
    }
}
