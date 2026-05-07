package es.refugio.refugio.application.command.adoptante;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;

/**
 * Command para modificar los datos de un Adoptante existente.
 * * @param id El identificador único del adoptante a editar.
 * @param dni Documento de identidad.
 * @param direccion Nueva dirección de residencia.
 * @param fechaNacimiento Fecha de nacimiento (para corregir errores de registro).
 * @param estadoValidacion El nuevo estado (usado por el Admin para aprobar/rechazar).
 */
public record EditAdoptanteCommand(
        AdoptanteId id,
        String fechaNacimiento, // <--- Añadido para mantener la consistencia
        String estadoValidacion
) {
}