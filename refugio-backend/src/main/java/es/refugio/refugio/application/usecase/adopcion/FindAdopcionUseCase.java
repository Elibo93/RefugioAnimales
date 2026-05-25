package es.refugio.refugio.application.usecase.adopcion;

import java.util.List;
import es.refugio.refugio.domain.error.AdopcionNotFoundException;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.repository.AdopcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Find Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FindAdopcionUseCase {

    private final AdopcionRepository adopcionRepository;

    public List<Adopcion> findAll() {
        return adopcionRepository.getAll();
    }

    public Page<Adopcion> findAll(Pageable pageable) {
        return adopcionRepository.findAll(pageable);
    }

    public Page<Adopcion> findFiltered(String q, String estado, Pageable pageable) {
        return adopcionRepository.findFiltered(q, estado, pageable);
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

    public List<Adopcion> findByCriteria(AdoptanteId adoptanteId, AnimalId animalId) {
        return adopcionRepository.findByCriteria(adoptanteId, animalId);
    }
}
