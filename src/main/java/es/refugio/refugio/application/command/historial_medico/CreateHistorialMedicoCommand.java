package es.refugio.refugio.application.command.historial_medico;

import java.time.LocalDateTime;

public record CreateHistorialMedicoCommand(
        Integer animalId,
        LocalDateTime fecha,
        String descripcion,
        String tratamiento,
        String veterinario
) {
}
