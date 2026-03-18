package es.refugio.animales.refugio.infraestructure.web.dto.persona;

import es.refugio.animales.refugio.domain.model.usuario.Persona;
import es.refugio.animales.refugio.infraestructure.web.validation.persona.NombradoPersona;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PersonaRequest(
        @NotBlank(message = "{Persona.valid.dni.no_vacio}") String dni,

        @NotBlank(message = "{Persona.valid.nombre.no_vacio}") @NombradoPersona(message = "{producto.valid.nombre.nombrado_validation}") String nombre,

        @NotBlank(message = "{Persona.valid.apellido.no_vacio}") String apellido,

        @Email(message = "{Persona.valid.email.valido}") String email,

        @NotBlank(message = "{Persona.valid.telefono.no_vacio}") String telefono,

        String direccion,

        String fechaNacimiento

) {

    // Constructor de conveniencia para crear un request desde el dominio
    public PersonaRequest(Persona p) {
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
