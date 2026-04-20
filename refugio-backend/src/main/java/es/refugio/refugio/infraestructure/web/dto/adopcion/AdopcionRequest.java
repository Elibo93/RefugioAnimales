package es.refugio.refugio.infraestructure.web.dto.adopcion;

import java.time.LocalDateTime;

public record AdopcionRequest(
        Integer animalId,
        Integer adoptanteId,
        LocalDateTime fechaAdopcion,
        String estado,
        String contrato
) {
}
