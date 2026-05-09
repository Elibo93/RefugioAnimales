package es.refugio.refugio.infraestructure.web.dto.solicitud_adopcion;

import java.time.LocalDateTime;

/**
 * DTO para la actualización de solicitudes de adopción.
 * No incluye animalId ni adoptanteId para garantizar la integridad del vínculo original.
 */
public record SolicitudAdopcionUpdateRequest(
        LocalDateTime fecha,
        String estado,
        String comentario,
        String comentarioAdmin
) {}
