package es.refugio.refugio.application.command.solicitud_adopcion;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.solicitud_adopcion.SolicitudAdopcionId;

public record EditSolicitudAdopcionCommand(
        SolicitudAdopcionId id,
        Integer animalId,
        Integer adoptanteId,
        LocalDateTime fecha,
        String estado,
        String comentario
) {
}
