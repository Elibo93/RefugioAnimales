package es.refugio.refugio.infraestructure.db.repository.mock.animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import es.refugio.refugio.domain.model.animal.Animal;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.animal.enums.Especie;
import es.refugio.refugio.domain.model.animal.enums.EstadoAnimal;
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
        return animales.keySet().stream()
                .mapToInt(AnimalId::getValue)
                .max()
                .orElse(0) + 1;
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

    @Override
    public Optional<Animal> getByChipId(String chipId) {
        return animales.values().stream()
                .filter(a -> a.getChipId().equalsIgnoreCase(chipId))
                .findFirst();
    }

    @Override
    public List<Animal> getByEstado(EstadoAnimal estado) {
        return animales.values().stream()
                .filter(a -> a.getEstado() == estado)
                .collect(Collectors.toList());
    }

    @Override
    public List<Animal> getByEspecie(Especie especie) {
        return animales.values().stream()
                .filter(a -> a.getEspecie() == especie)
                .collect(Collectors.toList());
    }

    @Override
    public List<Animal> findFiltered(String q, String especie, String tamano, List<String> edad, String sexo, Boolean urgencia) {
        return getAll(); // Esbozo (Stub), utilizado principalmente para la compilación
    }

    @Override
    public List<Animal> findTop3ByEstadoOrderByVisitasDesc(EstadoAnimal estado) {
        return animales.values().stream()
                .filter(a -> a.getEstado() == estado)
                .sorted((a1, a2) -> Integer.compare(
                        a2.getVisitas() != null ? a2.getVisitas() : 0, 
                        a1.getVisitas() != null ? a1.getVisitas() : 0))
                .limit(3)
                .collect(Collectors.toList());
    }

    @Override
    public void incrementarVisitas(AnimalId id) {
        Animal a = animales.get(id);
        if (a != null) {
            a.setVisitas((a.getVisitas() != null ? a.getVisitas() : 0) + 1);
        }
    }
}