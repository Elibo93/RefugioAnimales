package es.refugio.refugio.infraestructure.web.dto.usuario;

import es.refugio.refugio.domain.model.usuario.Usuario;
import es.refugio.refugio.infraestructure.web.validation.usuario.NombradoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
        @NotBlank(message = "{Persona.valid.dni.no_vacio}") String dni,

        @NotBlank(message = "{Persona.valid.nombre.no_vacio}") @NombradoUsuario(message = "{producto.valid.nombre.nombrado_validation}") String nombre,

        @NotBlank(message = "{Persona.valid.apellido.no_vacio}") String apellido,

        @Email(message = "{Persona.valid.email.valido}") String email,

        @NotBlank(message = "{Persona.valid.telefono.no_vacio}") String telefono,

        String direccion,

        String fechaNacimiento

) {

    // Constructor de conveniencia para crear un request desde el dominio
    public UsuarioRequest(Usuario p) {
        this(
                p.getDni(),
                p.getNombre(),
                p.getApellido(),
                p.getEmail(),
                p.getTelefono(),
                p.getDireccion(),
                p.getFechaNacimiento());
    }
}
