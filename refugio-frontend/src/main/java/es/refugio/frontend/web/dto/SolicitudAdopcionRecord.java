package es.refugio.frontend.web.dto;

import java.time.LocalDateTime;

public record SolicitudAdopcionRecord(
        Integer id,
        Integer animalId,
        Integer adoptanteId,
        LocalDateTime fecha,
        String estado,
        String comentario,
        String comentarioAdmin
) {}
