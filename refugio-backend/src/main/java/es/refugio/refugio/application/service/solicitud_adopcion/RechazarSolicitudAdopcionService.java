package es.refugio.refugio.application.service.solicitud_adopcion;

import es.refugio.refugio.application.usecase.solicitud_adopcion.RechazarSolicitudAdopcionUseCase;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Rechazar Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class RechazarSolicitudAdopcionService {

    private final RechazarSolicitudAdopcionUseCase useCase;

    public SolicitudAdopcion rechazar(SolicitudAdopcionId solicitudId, String comentario) {
        return useCase.rechazar(solicitudId, comentario);
    }
}
