package es.refugio.refugio.infraestructure.db.repository.mock.voluntario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.refugio.refugio.domain.model.usuario.UsuarioId;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.VoluntarioRepository;

public class VoluntarioRepositoryMockImpl implements VoluntarioRepository {

    private final Map<VoluntarioId, Voluntario> voluntarios = VoluntarioFactory.create();

    @Override
    public Voluntario save(Voluntario v) {
        if (v.getId() == null) {
            v.setId(new VoluntarioId(obtenerSiguienteId()));
        }
        voluntarios.put(v.getId(), v);
        return v;
    }

    private Integer obtenerSiguienteId() {
        return voluntarios.keySet().stream()
                .mapToInt(VoluntarioId::getValue)
                .max()
                .orElse(0) + 1;
    }

    @Override
    public Optional<Voluntario> getById(VoluntarioId id) {
        return Optional.ofNullable(voluntarios.get(id));
    }

    @Override
    public List<Voluntario> getAll() {
        return new ArrayList<>(voluntarios.values());
    }

    @Override
    public void deleteById(VoluntarioId id) {
        voluntarios.remove(id);
    }

    @Override
    public Optional<Voluntario> findByUsuarioId(UsuarioId usuarioId) {
        return voluntarios.values().stream()
                .filter(v -> v.getUsuarioId().equals(usuarioId))
                .findFirst();
    }
}
