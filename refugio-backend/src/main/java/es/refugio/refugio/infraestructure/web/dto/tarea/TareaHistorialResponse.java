package es.refugio.refugio.infraestructure.web.dto.tarea;

import java.time.LocalDateTime;

public record TareaHistorialResponse(
    Integer id,
    Integer tareaId,
    String estadoAnterior,
    String estadoNuevo,
    Integer usuarioId,
    String usuarioNombre,
    LocalDateTime fechaCambio,
    String observaciones
) {}
