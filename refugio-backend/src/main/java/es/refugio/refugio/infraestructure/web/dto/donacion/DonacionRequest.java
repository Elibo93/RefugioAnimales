package es.refugio.refugio.infraestructure.web.dto.donacion;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;

public record DonacionRequest(
                Integer usuarioId,
                Integer objetivoId,
                TipoDonacion tipo,
                Double cantidad,
                FrecuenciaDonacion frecuencia,
                
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
                LocalDateTime fecha,
                
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
                LocalDateTime proximaFechaPago,
                
                String descripcion) {
}
