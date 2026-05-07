package es.refugio.refugio.infraestructure.db.jpa.repository.perfil_legal;

import es.refugio.refugio.domain.model.perfil_legal.PerfilLegal;
import es.refugio.refugio.domain.model.perfil_legal.PerfilLegalId;
import es.refugio.refugio.domain.repository.PerfilLegalRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.PerfilLegalEntity;
import es.refugio.refugio.infraestructure.mapper.PerfilLegalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PerfilLegalJpaRepositoryImpl implements PerfilLegalRepository {

    private final PerfilLegalEntityJpaRepository jpaRepository;

    @Override
    public PerfilLegal save(PerfilLegal domain) {
        PerfilLegalEntity entity = PerfilLegalMapper.toEntity(domain);
        return PerfilLegalMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<PerfilLegal> getById(PerfilLegalId id) {
        return jpaRepository.findById(id.getValue())
                .map(PerfilLegalMapper::toDomain);
    }

    @Override
    public List<PerfilLegal> getAll() {
        return jpaRepository.findAll().stream()
                .map(PerfilLegalMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(PerfilLegalId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public Optional<PerfilLegal> findByUsuarioId(Integer usuarioId) {
        return jpaRepository.findByUsuarioId(usuarioId)
                .map(PerfilLegalMapper::toDomain);
    }

    @Override
    public Optional<PerfilLegal> findByDni(String dni) {
        return jpaRepository.findByDni(dni)
                .map(PerfilLegalMapper::toDomain);
    }
}
