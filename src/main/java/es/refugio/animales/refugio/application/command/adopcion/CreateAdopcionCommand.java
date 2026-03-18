package es.refugio.animales.refugio.application.command.adopcion;

import es.refugio.animales.refugio.domain.model.animal.AnimalId;
import es.refugio.animales.refugio.domain.model.usuario.PersonaId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class CreateAdopcionCommand {
    private PersonaId personaId;
    private AnimalId animalId;

}
