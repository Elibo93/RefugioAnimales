package es.refugio.refugio.infraestructure.db.repository.mock.animal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AnimalRepository;

public class AnimalRepositoryMockImpl implements AnimalRepository {

    private final Map<AnimalId, Animal> animales = AnimalFactory.getDemoData();

    @Override
    public Animal save(Animal a) {

        if (a.getId() == null) {
            a.setId(new AnimalId(obtenerSiguienteId()));
        }

        animales.put(a.getId(), a);

        return a;
    }

    private int obtenerSiguienteId() {

        AnimalId ultimo = null;

        if (!animales.isEmpty()) {

            Collection<Animal> lista = animales.values();

            for (Animal a : lista) {
                ultimo = a.getId();
            }
        }

        return (ultimo != null ? ultimo.getValue() + 1 : 1);
    }

    @Override
    public List<Animal> getAll() {
        return new ArrayList<>(animales.values());
    }

    @Override
    public Optional<Animal> getById(AnimalId id) {
        return Optional.ofNullable(animales.get(id));
    }

    @Override
    public void deleteById(AnimalId id) {
        animales.remove(id);
    }

}