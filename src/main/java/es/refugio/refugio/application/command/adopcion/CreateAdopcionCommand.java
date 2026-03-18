package es.refugio.refugio.application.command.adopcion;

import java.time.LocalDateTime;

public record CreateAdopcionCommand(
        Integer animalId,
        Integer adoptanteId,
        LocalDateTime fechaAdopcion,
        String estado,
        String contrato
) {
}
