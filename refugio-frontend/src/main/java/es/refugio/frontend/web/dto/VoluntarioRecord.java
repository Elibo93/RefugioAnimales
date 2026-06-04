package es.refugio.frontend.web.dto;

public record VoluntarioRecord(
        Integer id,
        Integer usuarioId,
        String disponibilidad,
        String especialidad,
        String estado
) {}
