package es.refugio.refugio.application.command.usuario;

import es.refugio.auth.domain.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class CreateUsuarioCommand {
    private String email;
    private String username;
    private String contrasena;
    private Rol rol;
}