package es.refugio.refugio.infraestructure.web.dto.voluntario;

import java.time.LocalDate;
import es.refugio.refugio.domain.model.voluntario.enums.TurnoDisponibilidad;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoDisponibilidad;

public record DisponibilidadResponse(
    Integer id,
    LocalDate fecha,
    TurnoDisponibilidad turno,
    EstadoDisponibilidad estado
) {}
