package es.refugio.frontend.web.dto;

import java.time.LocalDateTime;

public record DonacionRecord(
        Integer id,
        Integer usuarioId,
        Integer objetivoId,
        String tipo,
        Double cantidad,
        String frecuencia,
        LocalDateTime fecha,
        LocalDateTime proximaFechaPago,
        String descripcion
) {}
