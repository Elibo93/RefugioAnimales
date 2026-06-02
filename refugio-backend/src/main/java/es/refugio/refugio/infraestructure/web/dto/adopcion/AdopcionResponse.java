package es.refugio.refugio.infraestructure.web.dto.adopcion;

import java.time.LocalDateTime;

public record AdopcionResponse(
    Integer id,
    Integer animalId,
    Integer adoptanteId,
    Integer solicitudAdopcionId,
    LocalDateTime fechaAdopcion,
    String estado,
    String contrato
) {}
