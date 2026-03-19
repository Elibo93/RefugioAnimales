package es.refugio.refugio.application.command.voluntario;

public record CreateVoluntarioCommand(
        Integer usuarioId,
        String disponibilidad
) {
}
