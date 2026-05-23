package es.refugio.refugio.application.service.solicitud_adopcion;

import es.refugio.refugio.application.usecase.solicitud_adopcion.AprobarSolicitudAdopcionUseCase;
import es.refugio.refugio.domain.model.adopcion.Adopcion;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Aprobar Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class AprobarSolicitudAdopcionService {

    private final AprobarSolicitudAdopcionUseCase useCase;

    public Adopcion aprobar(SolicitudAdopcionId solicitudId) {
        return useCase.aprobar(solicitudId);
    }
}
