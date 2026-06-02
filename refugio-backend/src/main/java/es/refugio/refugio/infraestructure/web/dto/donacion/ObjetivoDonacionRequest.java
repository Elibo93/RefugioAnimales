package es.refugio.refugio.infraestructure.web.dto.donacion;

import java.time.LocalDateTime;
import es.refugio.refugio.domain.model.donacion.enums.EstadoObjetivo;
import es.refugio.refugio.domain.model.donacion.enums.PrioridadObjetivo;

public record ObjetivoDonacionRequest(
        String titulo,
        String descripcion,
        Double montoObjetivo,
        Double montoRecaudado,
        PrioridadObjetivo prioridad,
        EstadoObjetivo estado,
        LocalDateTime fechaLimite,
        String icono) {
}
