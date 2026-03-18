package es.refugio.refugio.infraestructure.db.repository.mock.voluntario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.refugio.refugio.domain.model.voluntario.Voluntario;
import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import es.refugio.refugio.domain.repository.VoluntarioRepository;

@Repository
public class VoluntarioRepositoryMockImpl implements VoluntarioRepository {

    private final Map<VoluntarioId, Voluntario> voluntarios = VoluntarioFactory.getDemoData();

    @Override
    public Voluntario save(Voluntario p) {
        // CREATE → si no tiene ID, generar uno nuevo
        if (p.getId() == null) {
            p.setId(new VoluntarioId(obtenerSiguienteId()));
        }

        voluntarios.put(p.getId(), p);
        return p;
    }

    private int obtenerSiguienteId() {
        VoluntarioId ultimo = null;

        if (!voluntarios.isEmpty()) {
            Collection<Voluntario> lista = voluntarios.values();

            for (Voluntario prof : lista) {
                ultimo = prof.getId();
            }
        }

        return (ultimo == null) ? 1 : ultimo.getValue() + 1;
    }

    @Override
    public List<Voluntario> getAll() {
        return new ArrayList<>(voluntarios.values());
    }

    @Override
    public Optional<Voluntario> getById(VoluntarioId id) {
        return Optional.ofNullable(voluntarios.get(id));
    }

    @Override
    public void deleteById(VoluntarioId id) {
        voluntarios.remove(id);
    }

    @Override
    public Optional<Voluntario> getByName(String nombre) {
        return voluntarios.values()
                .stream()
                .filter(p -> p.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

}

















