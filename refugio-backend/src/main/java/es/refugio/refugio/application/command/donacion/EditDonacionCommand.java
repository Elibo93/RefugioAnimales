package es.refugio.refugio.application.command.donacion;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.donacion.DonacionId;

public record EditDonacionCommand(
        DonacionId id,
        Integer usuarioId,
        String tipo,
        Double cantidad,
        String frecuencia,
        LocalDateTime fecha,
        LocalDateTime proximaFechaPago,
        String descripcion
) {
}
