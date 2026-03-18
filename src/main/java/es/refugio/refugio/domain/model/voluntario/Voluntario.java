package es.refugio.refugio.domain.model.voluntario;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Voluntario {

	// Atributos
	private VoluntarioId id;
	private String nombre;
	private String apellido;
	private String especialidad;
	private String email;
	private String telefono;
	private LocalDateTime createdAt;
}

















