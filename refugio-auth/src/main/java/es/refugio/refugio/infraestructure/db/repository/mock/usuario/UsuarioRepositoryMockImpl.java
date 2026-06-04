package es.refugio.refugio.infraestructure.db.repository.mock.usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.UsuarioRepository;

@Repository
public class UsuarioRepositoryMockImpl implements UsuarioRepository {
    
    private final Map<UsuarioId, Usuario> usuarios = UsuarioFactory.getDemoData();

    @Override
    public Usuario save(Usuario t) {
        if (t.getId() == null) {
            t.setId(new UsuarioId(obtenerSiguienteId()));
        }
        usuarios.put(t.getId(), t);
        return t;
    }

    private int obtenerSiguienteId() {
        return usuarios.keySet().stream()
                .mapToInt(UsuarioId::getValue)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public List<Usuario> getAll() {
        return new ArrayList<>(usuarios.values());
    }

    @Override
    public Optional<Usuario> getById(UsuarioId id) {
        return Optional.ofNullable(usuarios.get(id));
    }

    @Override
    public void deleteById(UsuarioId id) {
        usuarios.remove(id);
    }

    @Override
    public Optional<Usuario> getByEmail(String email) {
        return usuarios.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}