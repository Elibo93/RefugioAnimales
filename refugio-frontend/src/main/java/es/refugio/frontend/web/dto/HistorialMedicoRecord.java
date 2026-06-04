package es.refugio.frontend.web.dto;

import java.time.LocalDateTime;

public record HistorialMedicoRecord(
        Integer id,
        Integer animalId,
        LocalDateTime fecha,
        String descripcion,
        String tratamiento,
        String veterinario
) {}
