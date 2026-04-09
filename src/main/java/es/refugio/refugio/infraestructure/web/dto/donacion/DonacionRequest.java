package es.refugio.refugio.infraestructure.web.dto.donacion;

import java.time.LocalDateTime;

public record DonacionRequest(
        Integer usuarioId,
        String tipo,
        Double cantidad,
        String frecuencia,
        LocalDateTime fecha,
        String descripcion
) {
}

