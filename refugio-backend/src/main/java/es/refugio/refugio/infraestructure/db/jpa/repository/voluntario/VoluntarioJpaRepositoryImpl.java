package es.refugio.refugio.infraestructure.db.jpa.repository.voluntario;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.VoluntarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.VoluntarioEntity;
import es.refugio.refugio.infraestructure.mapper.VoluntarioMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VoluntarioJpaRepositoryImpl implements VoluntarioRepository {

    private final VoluntarioEntityJpaRepository jpaRepository;

    @Override
    public Voluntario save(Voluntario voluntario) {
        VoluntarioEntity entity = VoluntarioMapper.toEntity(voluntario);
        return VoluntarioMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Voluntario> getById(VoluntarioId id) {
        return jpaRepository.findById(id.getValue())
                .map(VoluntarioMapper::toDomain);
    }

    @Override
    public List<Voluntario> getAll() {
        return VoluntarioMapper.toDomain(jpaRepository.findAll());
    }

    @Override
    public void deleteById(VoluntarioId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public Optional<Voluntario> findByUsuarioId(UsuarioId usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId.getValue())
                .map(VoluntarioMapper::toDomain);
    }
}
