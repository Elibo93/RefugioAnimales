package es.refugio.refugio.infraestructure.web.dto.donacion;

import java.time.LocalDateTime;

public record DonacionRequest(
        Integer usuarioId,
        String tipo,
        Double cantidad,
        LocalDateTime fecha,
        String descripcion
) {
}
