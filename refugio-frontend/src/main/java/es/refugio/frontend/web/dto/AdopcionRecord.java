package es.refugio.frontend.web.dto;

import java.time.LocalDateTime;

public record AdopcionRecord(
        Integer id,
        Integer animalId,
        Integer adoptanteId,
        Integer solicitudAdopcionId,
        LocalDateTime fechaAdopcion,
        String estado,
        String contrato
) {}
