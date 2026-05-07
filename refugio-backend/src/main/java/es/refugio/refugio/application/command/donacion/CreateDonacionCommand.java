package es.refugio.refugio.application.command.donacion;

import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CreateDonacionCommand {
    private final Integer usuarioId;
    private final Integer objetivoId;
    private final TipoDonacion tipo;
    private final Double cantidad;
    private final FrecuenciaDonacion frecuencia;
    private final LocalDateTime fecha;
    private final LocalDateTime proximaFechaPago;
    private final String descripcion;
}
