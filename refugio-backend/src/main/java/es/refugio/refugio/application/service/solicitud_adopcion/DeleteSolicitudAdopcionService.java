package es.refugio.refugio.application.service.solicitud_adopcion;

import es.refugio.refugio.application.usecase.solicitud_adopcion.DeleteSolicitudAdopcionUseCase;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteSolicitudAdopcionService {

    private final DeleteSolicitudAdopcionUseCase useCase;

    public void delete(SolicitudAdopcionId id) {
        useCase.delete(id);
    }
}
