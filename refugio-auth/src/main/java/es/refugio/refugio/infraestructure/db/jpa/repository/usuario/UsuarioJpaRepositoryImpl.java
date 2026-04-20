package es.refugio.refugio.infraestructure.db.jpa.repository.usuario;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import es.refugio.refugio.infraestructure.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UsuarioJpaRepositoryImpl implements UsuarioRepository {

    private final UsuarioEntityJpaRepository repository;

    @Override
    public Usuario save(Usuario t) {
        UsuarioEntity usuarioEntity = UsuarioMapper.toEntity(t);
        return UsuarioMapper.toDomain(repository.save(usuarioEntity));
    }

    @Override
    public List<Usuario> getAll() {
        return UsuarioMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Usuario> getById(UsuarioId id) {
        return repository.findById(id.getValue())
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public void deleteById(UsuarioId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public Optional<Usuario> getByEmail(String email) {
        return repository.findByEmail(email)
                .map(UsuarioMapper::toDomain);
    }
}