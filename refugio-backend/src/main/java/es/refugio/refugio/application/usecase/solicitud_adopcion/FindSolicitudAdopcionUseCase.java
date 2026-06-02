package es.refugio.refugio.application.usecase.solicitud_adopcion;

import java.util.List;
import es.refugio.refugio.domain.error.SolicitudAdopcionNotFoundException;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import es.refugio.refugio.domain.repository.SolicitudAdopcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
/**
 * Caso de uso que encapsula la lógica de negocio de la aplicación para Find Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class FindSolicitudAdopcionUseCase {

    private final SolicitudAdopcionRepository solicitudAdopcionRepository;

    public List<SolicitudAdopcion> findAll() {
        List<SolicitudAdopcion> solicitudes = solicitudAdopcionRepository.getAll();
        if (solicitudes.isEmpty()) {
            throw new SolicitudAdopcionNotFoundException();
        }
        return solicitudes;
    }

    public Page<SolicitudAdopcion> findAll(Pageable pageable) {
        return solicitudAdopcionRepository.findAll(pageable);
    }

    public SolicitudAdopcion findById(SolicitudAdopcionId id) {
        return solicitudAdopcionRepository.getById(id)
                .orElseThrow(() -> new SolicitudAdopcionNotFoundException(id.getValue()));
    }

    public List<SolicitudAdopcion> findByAnimalId(AnimalId animalId) {
        return solicitudAdopcionRepository.getByAnimalId(animalId);
    }

    public List<SolicitudAdopcion> findByAdoptanteId(AdoptanteId adoptanteId) {
        return solicitudAdopcionRepository.getByAdoptanteId(adoptanteId);
    }
}
