package es.refugio.refugio.application.command.solicitud_adopcion;

import java.time.LocalDateTime;

public record CreateSolicitudAdopcionCommand(
        Integer animalId,
        Integer adoptanteId,
        LocalDateTime fecha,
        String comentario
) {
}
