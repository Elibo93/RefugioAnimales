package es.refugio.refugio.infraestructure.db.repository.mock.adopcion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.repository.AdopcionRepository;

public class AdopcionRepositoryMockImpl implements AdopcionRepository {

    private final Map<AdopcionId, Adopcion> adopciones = AdopcionFactory.getDemoData();

    @Override
    public Adopcion save(Adopcion i) {
        if (i.getId() == null) {
            i.setId(new AdopcionId(obtenerSiguienteId()));
        }
        adopciones.put(i.getId(), i);
        return i;
    }

    private int obtenerSiguienteId() {
        return adopciones.keySet().stream()
                .mapToInt(AdopcionId::getValue)
                .max()
                .orElse(0) + 1;
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

    @Override
    public List<Adopcion> getByAdoptanteId(AdoptanteId adoptanteId) {
        return adopciones.values().stream()
                .filter(a -> a.getAdoptanteId().equals(adoptanteId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Adopcion> getByAnimalId(AnimalId animalId) {
        return adopciones.values().stream()
                .filter(a -> a.getAnimalId().equals(animalId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Adopcion> getByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId) {
        return adopciones.values().stream()
                .filter(a -> a.getAdoptanteId().equals(adoptanteId) && a.getAnimalId().equals(animalId))
                .findFirst();
    }

    @Override
    public boolean existsByAdoptanteAndAnimal(AdoptanteId adoptanteId, AnimalId animalId) {
        return adopciones.values().stream()
                .anyMatch(a -> a.getAdoptanteId().equals(adoptanteId) && a.getAnimalId().equals(animalId));
    }
}