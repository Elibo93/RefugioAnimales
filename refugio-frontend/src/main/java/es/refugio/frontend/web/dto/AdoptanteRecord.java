package es.refugio.frontend.web.dto;

import java.time.LocalDateTime;

public record AdoptanteRecord(
        int id,
        Integer usuarioId,
        String estadoValidacion,
        LocalDateTime fechaRegistro
) {}
