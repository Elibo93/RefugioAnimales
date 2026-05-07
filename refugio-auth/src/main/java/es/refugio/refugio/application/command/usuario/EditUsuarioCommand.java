package es.refugio.refugio.application.command.usuario;

import es.refugio.auth.domain.Rol;
import es.refugio.refugio.domain.model.usuario.UsuarioId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class EditUsuarioCommand {

    private UsuarioId id;
    private String email;
    private String username;
    private Rol rol;
}