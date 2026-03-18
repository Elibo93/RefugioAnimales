package es.refugio.refugio.application.command.adoptante;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;

/**
 * Command para modificar los datos de un Adoptante existente.
 * * @param id El identificador único del adoptante a editar.
 * @param dni Documento de identidad (por si hubo un error al registrarlo).
 * @param direccion Nueva dirección de residencia.
 * @param estadoValidacion El nuevo estado (usado principalmente por el Admin).
 */
public record EditAdoptanteCommand(
        AdoptanteId id,
        String dni,
        String direccion,
        String estadoValidacion
) {
}