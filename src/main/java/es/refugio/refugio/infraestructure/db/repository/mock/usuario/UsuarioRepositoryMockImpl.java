package es.refugio.refugio.infraestructure.db.repository.mock.usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.repository.UsuarioRepository;

@Repository
public class UsuarioRepositoryMockImpl implements UsuarioRepository {
    private final Map<UsuarioId, Usuario> personas = UsuarioFactory.getDemoData();

    @Override
    public Usuario save(Usuario t) {
        // create
        if (t.getId() == null)
            t.setId(new UsuarioId(obtenerSiguienteId()));

        personas.put(t.getId(), t);
        return t;
    }

    private int obtenerSiguienteId() {
        UsuarioId ultimo = null;
        if (!personas.isEmpty()) {
            Collection<Usuario> lista = personas.values();

            for (Usuario p : lista) {
                ultimo = p.getId();
            }

        }
        return ultimo.getValue() + 1;
    }

    @Override
    public List<Usuario> getAll() {
        return new ArrayList<>(personas.values());
    }

    @Override
    public Optional<Usuario> getById(UsuarioId id) {
        // Un optional puede tener una valor o no. Si no existe el Persona devuelve
        // Optional.empty
        return Optional.ofNullable(personas.get(id));
    }

    @Override
    public void deleteById(UsuarioId id) {
        personas.remove(id);
    }

    @Override
    public Optional<Usuario> getByName(String name) {
        // TODO Sin implementar
        throw new UnsupportedOperationException("Unimplemented method 'getByName'");
    }

}
