package es.refugio.refugio.application.command.historial_medico;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.historial_medico.HistorialMedicoId;

public record EditHistorialMedicoCommand(
        HistorialMedicoId id,
        Integer animalId,
        LocalDateTime fecha,
        String descripcion,
        String tratamiento,
        String veterinario
) {
}
