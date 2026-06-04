package es.refugio.refugio.application.command.adoptante;

import es.refugio.refugio.domain.model.adoptante.AdoptanteId;

/**
 * Command para modificar los datos de un Adoptante existente.
 * * @param id El identificador único del adoptante a editar.
 * @param estadoValidacion El nuevo estado (usado por el Admin para aprobar/rechazar).
 */
public record EditAdoptanteCommand(
        AdoptanteId id,
        String estadoValidacion
) {
}