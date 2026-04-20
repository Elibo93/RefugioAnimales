package es.refugio.refugio.application.command.adopcion;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.adopcion.AdopcionId;

public record EditAdopcionCommand(
        AdopcionId id,
        Integer animalId,
        Integer adoptanteId,
        LocalDateTime fechaAdopcion,
        String estado,
        String contrato
) {
}
