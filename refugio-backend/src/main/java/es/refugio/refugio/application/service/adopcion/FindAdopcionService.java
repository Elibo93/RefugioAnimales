package es.refugio.refugio.application.service.adopcion;

import java.util.List;
import es.refugio.refugio.application.usecase.adopcion.FindAdopcionUseCase;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Find Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FindAdopcionService {

    private final FindAdopcionUseCase useCase;

    public List<Adopcion> findAll() {
        return useCase.findAll();
    }

    public Page<Adopcion> findAll(Pageable pageable) {
        return useCase.findAll(pageable);
    }

    public Page<Adopcion> findFiltered(String q, String estado, Pageable pageable) {
        return useCase.findFiltered(q, estado, pageable);
    }

    public Adopcion findById(AdopcionId id) {
        return useCase.findById(id);
    }

    public List<Adopcion> findByAnimalId(AnimalId animalId) {
        return useCase.findByAnimalId(animalId);
    }

    public List<Adopcion> findByAdoptanteId(AdoptanteId adoptanteId) {
        return useCase.findByAdoptanteId(adoptanteId);
    }

    public List<Adopcion> findByCriteria(AdoptanteId adoptanteId, AnimalId animalId) {
        return useCase.findByCriteria(adoptanteId, animalId);
    }
}
