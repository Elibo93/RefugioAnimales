package es.refugio.refugio.application.command.adopcion;

import es.refugio.refugio.domain.model.adopcion.AdopcionId;
import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class EditAdopcionCommand {
    private AdopcionId id;
    private UsuarioId usuarioId;
    private AnimalId animalId;

}
