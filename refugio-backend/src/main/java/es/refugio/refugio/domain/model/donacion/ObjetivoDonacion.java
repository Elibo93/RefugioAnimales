package es.refugio.refugio.domain.model.donacion;

import java.time.LocalDateTime;

import es.refugio.refugio.domain.model.donacion.enums.EstadoObjetivo;
import es.refugio.refugio.domain.model.donacion.enums.PrioridadObjetivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjetivoDonacion {
    private ObjetivoDonacionId id;
    private String titulo;
    private String descripcion;
    private Double montoObjetivo;
    private Double montoRecaudado;
    private PrioridadObjetivo prioridad;
    private EstadoObjetivo estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaLimite;
    private String icono;
}
