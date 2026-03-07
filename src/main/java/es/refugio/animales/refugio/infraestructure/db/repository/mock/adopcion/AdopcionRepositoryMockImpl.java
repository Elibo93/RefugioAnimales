package es.refugio.animales.refugio.infraestructure.db.repository.mock.adopcion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.refugio.animales.refugio.domain.model.adopcion.Adopcion;
import es.refugio.animales.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.animales.refugio.domain.repository.AdopcionRepository;

public class AdopcionRepositoryMockImpl implements AdopcionRepository {

    private final Map<AdopcionId, Adopcion> adopciones = AdopcionFactory.getDemoData();

    @Override
    public Adopcion save(Adopcion i) {

        // Crear
        if (i.getId() == null)
            i.setId(new AdopcionId(obtenerSiguienteId()));

        adopciones.put(i.getId(), i);

        return i;
    }

    private int obtenerSiguienteId() {
        AdopcionId ultimo = null;

        if (!adopciones.isEmpty()) {
            Collection<Adopcion> lista = adopciones.values();

            for (Adopcion p : lista) {
                ultimo = p.getId();
            }
        }

        return (ultimo != null ? ultimo.getValue() + 1 : 1);
    }

    @Override
    public List<Adopcion> getAll() {
        return new ArrayList<>(adopciones.values());
    }

    @Override
    public Optional<Adopcion> getById(AdopcionId id) {
        return Optional.ofNullable(adopciones.get(id));
    }

    @Override
    public void deleteById(AdopcionId id) {
        adopciones.remove(id);
    }

    // @Override
    // public List<Adopcion> getByPersonaId(PersonaId personaId) {
    // List<Adopcion> result = new ArrayList<>();

    // for (Adopcion i : adopciones.values()) {
    // if (i.getPersonaId().equals(PersonaId)) {
    // result.add(i);
    // }
    // }

    // return result;
    // }

    // @Override
    // public List<Adopcion> getByAnimalId(AnimalId animalId) {
    // List<Adopcion> result = new ArrayList<>();

    // for (Adopcion i : adopciones.values()) {
    // if (i.getAnimalId().equals(AnimalId)) {
    // result.add(i);
    // }
    // }

    // return result;
    // }

    // @Override
    // public Optional<Adopcion> getByPersonaAndAnimal(PersonaId personaId, AnimalId
    // AnimalId) {

    // for (Adopcion i : adopciones.values()) {
    // if (i.getPersonaId().equals(PersonaId) &&
    // i.getAnimalId().equals(AnimalId)) {
    // return Optional.of(i);
    // }
    // }

    // return Optional.empty();
    // }

    // @Override
    // public boolean existsByPersonaAndAnimal(PersonaId personaId, AnimalId AnimalId)
    // {

    // for (Adopcion i : adopciones.values()) {
    // if (i.getPersonaId().equals(PersonaId) &&
    // i.getAnimalId().equals(AnimalId)) {
    // return true;
    // }
    // }

    // return false;
    // }
    @Override
    public List<Adopcion> getByPersonaId(Integer PersonaId) {
        List<Adopcion> result = new ArrayList<>();

        for (Adopcion i : adopciones.values()) {
            if (i.getPersonaId().getValue().equals(PersonaId)) {
                result.add(i);
            }
        }

        return result;
    }

    @Override
    public List<Adopcion> getByAnimalId(Integer AnimalId) {
        List<Adopcion> result = new ArrayList<>();

        for (Adopcion i : adopciones.values()) {
            if (i.getAnimalId().getValue().equals(AnimalId)) {
                result.add(i);
            }
        }

        return result;
    }
}
















