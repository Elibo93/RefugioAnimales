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

@RequiredArgsConstructor
public class DonacionJpaRepositoryImpl implements DonacionRepository {

    private final DonacionEntityJpaRepository jpaRepository;

    @Override
    public Donacion save(Donacion donacion) {
        DonacionEntity entity = DonacionMapper.toEntity(donacion);
        return DonacionMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Donacion> getById(DonacionId id) {
        return jpaRepository.findById(id.getValue())
                .map(DonacionMapper::toDomain);
    }

    @Override
    public List<Donacion> getAll() {
        return DonacionMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public void deleteById(DonacionId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public List<Donacion> getByUsuarioId(UsuarioId usuarioId) {
        return DonacionMapper.toDomain(jpaRepository.findByUsuarioId(usuarioId.getValue()));
    }
}
