package es.refugio.animales.refugio.domain.model.adopcion;

import java.time.LocalDateTime;

import es.refugio.animales.refugio.domain.model.persona.PersonaId;
import es.refugio.animales.refugio.domain.model.animal.AnimalId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Adopcion {
    private AdopcionId id;
    private PersonaId personaId;
    private AnimalId animalId;
    private LocalDateTime createdAt;

}

















