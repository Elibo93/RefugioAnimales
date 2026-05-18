package es.refugio.frontend.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TareaRecord(
        Integer id,
        String descripcion,
        LocalDateTime fecha,
        String estado,
        LocalDateTime fechaLimite,
        String instrucciones,
        String prioridad,
        List<Integer> voluntarioIds
) {}
