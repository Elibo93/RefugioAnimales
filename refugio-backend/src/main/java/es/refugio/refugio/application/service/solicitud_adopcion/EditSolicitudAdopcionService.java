package es.refugio.refugio.application.service.solicitud_adopcion;

import es.refugio.refugio.application.command.solicitud_adopcion.EditSolicitudAdopcionCommand;
import es.refugio.refugio.application.usecase.solicitud_adopcion.EditSolicitudAdopcionUseCase;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcion;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
/**
 * Servicio de aplicación que orquesta las operaciones relacionadas con Edit Solicitud Adopcion.
 *
 * @author Elisabeth
 * @author Diego
 */
public class EditSolicitudAdopcionService {

    private final EditSolicitudAdopcionUseCase useCase;

    public SolicitudAdopcion update(EditSolicitudAdopcionCommand command) {
        return useCase.update(command);
    }
}
