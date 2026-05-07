package es.refugio.refugio.infraestructure.web.dto.donacion;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;

public record DonacionRequest(
        Integer usuarioId,
        Integer objetivoId,
        TipoDonacion tipo,
        Double cantidad,
        FrecuenciaDonacion frecuencia,
        LocalDateTime fecha,
        String descripcion) {
}
