package es.refugio.refugio.application.command.voluntario;

import es.refugio.refugio.domain.model.usuario.UsuarioId;

public record CreateVoluntarioCommand(
                UsuarioId usuarioId,
                String disponibilidad) {
}
