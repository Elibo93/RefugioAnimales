package es.refugio.refugio.domain.model.donacion;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.donacion.enums.FrecuenciaDonacion;
import es.refugio.refugio.domain.model.donacion.enums.TipoDonacion;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Donacion {

    private DonacionId id;
    private UsuarioId usuarioId;
    private TipoDonacion tipo;
    private Double cantidad;
    private FrecuenciaDonacion frecuencia;
    private LocalDateTime fecha;
    private String descripcion;

}

