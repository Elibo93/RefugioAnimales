package es.refugio.refugio.application.command.solicitud_adopcion;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;

/**
 * Comando de edición refinado: solo permite cambiar datos de seguimiento y estado.
 */
public record EditSolicitudAdopcionCommand(
        SolicitudAdopcionId id,
        LocalDateTime fecha,
        String estado,
        String comentario
) {
}
