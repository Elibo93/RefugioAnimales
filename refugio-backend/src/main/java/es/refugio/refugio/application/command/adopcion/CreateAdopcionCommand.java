package es.refugio.refugio.application.command.adopcion;

import es.refugio.refugio.domain.model.adopcion.enums.EstadoAdopcion;

public record CreateAdopcionCommand(
                Integer adoptanteId,
                Integer animalId,
                EstadoAdopcion estado,
                String contrato) {
}
