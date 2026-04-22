package es.refugio.refugio.infraestructure.web.dto.voluntario;

/**
 * DTO para la actualización de voluntarios.
 * Solo permite modificar la disponibilidad, protegiendo el usuarioId vinculado.
 */
public record VoluntarioUpdateRequest(
        String disponibilidad
) {}
