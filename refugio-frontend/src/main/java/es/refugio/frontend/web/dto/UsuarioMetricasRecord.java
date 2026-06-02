package es.refugio.frontend.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UsuarioMetricasRecord(
    Long usuarioId,
    int tareasCompletadas,
    BigDecimal totalDonado,
    LocalDateTime fechaPrimerAporte,
    LocalDateTime ultimaActualizacion
) {}
