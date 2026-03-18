package es.refugio.refugio.application.command.adopcion;

import es.refugio.refugio.domain.model.animal.AnimalId;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class CreateAdopcionCommand {
    private UsuarioId usuarioId;
    private AnimalId animalId;

}
