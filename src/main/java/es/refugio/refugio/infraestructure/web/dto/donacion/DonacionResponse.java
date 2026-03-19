package es.refugio.refugio.infraestructure.web.dto.donacion;

import java.time.LocalDateTime;

public record DonacionResponse(
        Integer id,
        Integer usuarioId,
        String tipo,
        Double cantidad,
        LocalDateTime fecha,
        String descripcion
) {
}
