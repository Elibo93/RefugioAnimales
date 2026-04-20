package es.refugio.refugio.application.service.solicitud_adopcion;

import java.util.List;
import es.refugio.refugio.application.usecase.solicitud_adopcion.FindSolicitudAdopcionUseCase;
import es.refugio.refugio.domain.model.adoptante.AdoptanteId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FindSolicitudAdopcionService {

    private final FindSolicitudAdopcionUseCase useCase;

    public List<SolicitudAdopcion> findAll() {
        return useCase.findAll();
    }

    public SolicitudAdopcion findById(SolicitudAdopcionId id) {
        return useCase.findById(id);
    }

    public List<SolicitudAdopcion> findByAnimalId(AnimalId animalId) {
        return useCase.findByAnimalId(animalId);
    }

    public List<SolicitudAdopcion> findByAdoptanteId(AdoptanteId adoptanteId) {
        return useCase.findByAdoptanteId(adoptanteId);
    }
}
