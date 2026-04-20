package es.refugio.refugio.infraestructure.web.dto.historial_medico;

import java.time.LocalDateTime;

public record HistorialMedicoRequest(
        Integer animalId,
        LocalDateTime fecha,
        String descripcion,
        String tratamiento,
        String veterinario
) {
}
