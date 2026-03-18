package es.refugio.refugio.infraestructure.db.jpa.repository.usuario;

import java.util.List;
import java.util.Optional;

import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.UsuarioRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import es.refugio.refugio.infraestructure.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsuarioJpaRepositoryImpl implements UsuarioRepository {
    private final UsuarioEntityJpaRepository repository;

    @Override
    public Usuario save(Usuario t) {

        UsuarioEntity prod = UsuarioMapper.toEntity(t);
        return UsuarioMapper.toDomain(repository.save(prod));
    }

    @Override
    public List<Usuario> getAll() {
        return UsuarioMapper.toDomain(repository.findAll());
    }

    @Override
    public Optional<Usuario> getById(UsuarioId id) {
        Optional<UsuarioEntity> pe = repository.findById(id.getValue());

        if (pe.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(UsuarioMapper.toDomain(pe.get()));
    }

    @Override
    public void deleteById(UsuarioId id) {
        repository.deleteById(id.getValue());
    }

    @Override
    public Optional<Usuario> getByName(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByName'");
    }
    // Hereda automáticamente métodos como: save(), findById(), findAll(), delete(),
    // etc.

}
