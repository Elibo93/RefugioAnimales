package es.refugio.refugio.application.command.donacion;

import java.time.LocalDateTime;

public record CreateDonacionCommand(
        Integer usuarioId,
        String tipo,
        Double cantidad,
        LocalDateTime fecha,
        String descripcion
) {
}
