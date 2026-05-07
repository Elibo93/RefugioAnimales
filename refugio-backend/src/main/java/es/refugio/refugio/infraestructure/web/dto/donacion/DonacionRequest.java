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
                
                @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
                LocalDateTime fecha,
                
                @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
                LocalDateTime proximaFechaPago,
                
                String descripcion) {
}
