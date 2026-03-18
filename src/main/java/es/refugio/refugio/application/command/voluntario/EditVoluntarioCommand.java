package es.refugio.refugio.application.command.voluntario;

import es.refugio.refugio.domain.model.voluntario.VoluntarioId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class EditVoluntarioCommand {

    // Atributos
    private VoluntarioId id;
    private String especialidad;
    private String email;
    private String telefono;

}

















