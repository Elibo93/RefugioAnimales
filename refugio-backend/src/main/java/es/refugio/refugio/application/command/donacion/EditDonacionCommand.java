package es.refugio.refugio.application.command.donacion;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.donacion.DonacionId;
import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;

public record EditDonacionCommand(
        DonacionId id,
        Integer usuarioId,
        Integer objetivoId,
        TipoDonacion tipo,
        Double cantidad,
        FrecuenciaDonacion frecuencia,
        LocalDateTime fecha,
        String descripcion) {
}
