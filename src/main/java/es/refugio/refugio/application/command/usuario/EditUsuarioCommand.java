package es.refugio.refugio.application.command.usuario;

import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class EditUsuarioCommand {

    private UsuarioId id;
    private String nombre;   
    private String apellido;
    private String email;
    private String telefono;
}