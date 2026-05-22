package es.refugio.frontend.web.dto;

import java.time.LocalDate;

public record DisponibilidadRecord(
        String id,
        LocalDate fecha,
        String turno,
        String estado
) {
}
