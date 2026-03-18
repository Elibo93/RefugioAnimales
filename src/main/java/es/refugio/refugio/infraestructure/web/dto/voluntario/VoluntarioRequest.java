package es.refugio.refugio.infraestructure.web.dto.voluntario;

import jakarta.validation.constraints.NotBlank;
import es.refugio.refugio.domain.model.voluntario.Voluntario;
import jakarta.validation.constraints.Email;

public record VoluntarioRequest(
		@NotBlank(message = "{Voluntario.valid.nombre.no_vacio}") String nombre,
		@NotBlank(message = "{Voluntario.valid.apellido.no_vacio}") String apellido,
		@NotBlank(message = "{Voluntario.valid.especialidad.no_vacio}") String especialidad,
		@NotBlank(message = "{Voluntario.valid.email.no_vacio}") @Email(message = "{common.error.email_no_valido}") String email,
		@NotBlank(message = "{Voluntario.valid.telefono.no_vacio}") String telefono) {

	public VoluntarioRequest(Voluntario p) {
		this(
				p.getNombre(),
				p.getApellido(),
				p.getEspecialidad(),
				p.getEmail(),
				p.getTelefono());
	}

}
















