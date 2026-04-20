package es.refugio.refugio.infraestructure.web.dto.usuario;

import es.refugio.auth.domain.Rol;
import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.infraestructure.web.validation.usuario.NombradoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank(message = "{Usuario.valid.nombre.no_vacio}") @NombradoUsuario(message = "{usuario.valid.nombre.nombrado_validation}") String nombre,

        @NotBlank(message = "{Usuario.valid.apellido.no_vacio}") String apellido,

        @NotBlank(message = "{Usuario.valid.email.no_vacio}") @Email(message = "{Usuario.valid.email.valido}") String email,

        @NotBlank(message = "{Usuario.valid.password.no_vacio}") String contrasena,

        @NotBlank(message = "{Usuario.valid.telefono.no_vacio}") String telefono,

        @NotNull(message = "{Usuario.valid.rol.no_nulo}") Rol rol) {

    public UsuarioRequest(Usuario u) {
        this(
                u.getNombre(),
                u.getApellido(),
                u.getEmail(),
                u.getContrasena(),
                u.getTelefono(),
                u.getRol());
    }
}