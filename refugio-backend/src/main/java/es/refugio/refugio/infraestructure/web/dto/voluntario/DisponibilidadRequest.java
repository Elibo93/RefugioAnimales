package es.refugio.refugio.infraestructure.web.dto.voluntario;

import java.time.LocalDate;
import es.refugio.refugio.domain.model.voluntario.enums.TurnoDisponibilidad;
import es.refugio.refugio.domain.model.voluntario.enums.EstadoDisponibilidad;
import jakarta.validation.constraints.NotNull;

public record DisponibilidadRequest(
    @NotNull LocalDate fecha,
    @NotNull TurnoDisponibilidad turno,
    @NotNull EstadoDisponibilidad estado
) {}
