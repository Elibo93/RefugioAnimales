package es.refugio.refugio.application.command.donacion;

import java.time.LocalDateTime;

public record CreateDonacionCommand(
        Integer usuarioId,
        String tipo,
        Double cantidad,
        String frecuencia,
        LocalDateTime fecha,
        LocalDateTime proximaFechaPago,
        String descripcion
) {
}
