package es.refugio.refugio.application.command.adoptante;

/**
 * Command para la creación de un perfil de Adoptante.
 * * @param usuarioId El ID del usuario al que se vincula este perfil (FK).
 */
public record CreateAdoptanteCommand(
        Integer usuarioId
) {
}