package es.refugio.refugio.infraestructure.web.dto.adopcion;

import es.refugio.refugio.domain.model.adopcion.Adopcion;
import jakarta.validation.constraints.NotNull;

public record AdopcionRequest(

    @NotNull(message = "{Adopcion.valid.PersonaId.no_nulo}")
    Integer PersonaId,

    @NotNull(message = "{Adopcion.valid.AnimalId.no_nulo}")
    Integer AnimalId
    
) {

    // Constructor de conveniencia desde el dominio
    public AdopcionRequest(Adopcion i) {
        this(
            i.getUsuarioId().getValue(),
            i.getAnimalId().getValue()
        );
    }
}
















