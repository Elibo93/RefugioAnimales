package es.refugio.animales.refugio.application.command.persona;

import es.refugio.animales.refugio.domain.model.persona.PersonaId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class EditPersonaCommand {

    private PersonaId id;
    private String email;
    private String telefono;
    private String direccion;

}
