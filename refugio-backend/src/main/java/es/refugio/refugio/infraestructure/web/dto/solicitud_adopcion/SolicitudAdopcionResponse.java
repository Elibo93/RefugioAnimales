package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import java.time.LocalDateTime;

public record SolicitudAdopcionResponse(
        Integer id,
        Integer animalId,
        Integer adoptanteId,
        LocalDateTime fecha,
        String estado,
        String comentario
) {
}
