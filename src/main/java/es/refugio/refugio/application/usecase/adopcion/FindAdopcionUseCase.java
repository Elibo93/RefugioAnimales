package es.refugio.refugio.application.usecase.adopcion;

import java.util.List;
import es.refugio.refugio.domain.error.AdopcionNotFoundException;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindAdopcionUseCase {

    private final AdopcionRepository adopcionRepository;

    public List<Adopcion> findAll() {
        List<Adopcion> adopciones = adopcionRepository.getAll();
        if (adopciones.isEmpty()) {
            throw new AdopcionNotFoundException();
        }
        return adopciones;
    }

    public Adopcion findById(AdopcionId id) {
        return adopcionRepository.getById(id)
                .orElseThrow(() -> new AdopcionNotFoundException(id.getValue()));
    }

    public List<Adopcion> findByAnimalId(AnimalId animalId) {
        return adopcionRepository.getByAnimalId(animalId);
    }

    public List<Adopcion> findByAdoptanteId(AdoptanteId adoptanteId) {
        return adopcionRepository.getByAdoptanteId(adoptanteId);
    }
}
