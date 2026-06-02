package es.refugio.refugio.infraestructure.web.dto.donacion;

import java.time.LocalDateTime;

public record ObjetivoDonacionResponse(
        Integer id,
        String titulo,
        String descripcion,
        Double montoObjetivo,
        Double montoRecaudado,
        String prioridad,
        String estado,
        LocalDateTime fechaInicio,
        LocalDateTime fechaLimite,
        String icono) {
}
