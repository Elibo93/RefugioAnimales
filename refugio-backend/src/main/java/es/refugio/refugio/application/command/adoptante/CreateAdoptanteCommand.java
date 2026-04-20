package es.refugio.refugio.application.command.adoptante;

/**
 * Command para la creación de un perfil de Adoptante.
 * * @param usuarioId El ID del usuario al que se vincula este perfil (FK).
 * @param dni Documento nacional de identidad.
 * @param direccion Domicilio del adoptante.
 */
public record CreateAdoptanteCommand(
        Integer usuarioId,
        String dni,
        String direccion,
        String fechaNacimiento
) {
}