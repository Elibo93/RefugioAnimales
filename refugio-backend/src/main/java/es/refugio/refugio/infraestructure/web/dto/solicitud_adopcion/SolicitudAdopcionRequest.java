package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import java.time.LocalDateTime;

public record SolicitudAdopcionRequest(
        Integer animalId,
        Integer adoptanteId,
        LocalDateTime fecha,
        String estado,
        String comentario
) {
}
