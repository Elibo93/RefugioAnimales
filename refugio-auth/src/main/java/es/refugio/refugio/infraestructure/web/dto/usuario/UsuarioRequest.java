package es.refugio.refugio.infraestructure.web.dto.usuario;

import es.refugio.auth.domain.Rol;
import es.refugio.refugio.domain.model.usuario.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
        @NotBlank(message = "{Usuario.valid.email.no_vacio}") @Email(message = "{Usuario.valid.email.valido}") String email,

        @NotBlank(message = "El nombre de usuario no puede estar vacío") String username,

        String contrasena,
        Rol rol) {

    public UsuarioRequest(Usuario u) {
        this(
                u.getEmail(),
                u.getUsername(),
                u.getContrasena(),
                u.getRol());
    }
}