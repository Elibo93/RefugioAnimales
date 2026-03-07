package es.refugio.animales.refugio.infraestructure.db.repository.mock.persona;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.refugio.animales.refugio.domain.model.persona.Persona;
import es.refugio.animales.refugio.domain.model.persona.PersonaId;
import es.refugio.animales.refugio.domain.repository.PersonaRepository;

@Repository
public class PersonaRepositoryMockImpl implements PersonaRepository {
    private final Map<PersonaId, Persona> personas = PersonaFactory.getDemoData();

    @Override
    public Persona save(Persona t) {
        // create
        if (t.getId() == null)
            t.setId(new PersonaId(obtenerSiguienteId()));

        personas.put(t.getId(), t);
        return t;
    }

    private int obtenerSiguienteId() {
        PersonaId ultimo = null;
        if (!personas.isEmpty()) {
            Collection<Persona> lista = personas.values();

            for (Persona p : lista) {
                ultimo = p.getId();
            }

        }
        return ultimo.getValue() + 1;
    }

    @Override
    public List<Persona> getAll() {
        return new ArrayList<>(personas.values());
    }

    @Override
    public Optional<Persona> getById(PersonaId id) {
        // Un optional puede tener una valor o no. Si no existe el Persona devuelve
        // Optional.empty
        return Optional.ofNullable(personas.get(id));
    }

    @Override
    public void deleteById(PersonaId id) {
        personas.remove(id);
    }

    @Override
    public Optional<Persona> getByName(String name) {
        // TODO Sin implementar
        throw new UnsupportedOperationException("Unimplemented method 'getByName'");
    }

}
















